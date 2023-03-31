(ns dinsro.actions.nostr.events
  (:require
   [clojure.core.async :as async]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.actions.nostr.connections :as a.n.connections]
   [dinsro.actions.nostr.event-tags :as a.n.event-tags]
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.queries.nostr.event-tags :as q.n.event-tags]
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/events.cljc][Event Model]]
;; [[../../queries/nostr/events.clj][Event Queries]]
;; [[../../ui/nostr/events.cljs][Event UI]]

(defn update-event!
  [m]
  (log/info :update-pubkey!/starting {:m m})
  (let [{:keys      [req-id tags id created-at
                     kind sig content]
         pubkey-hex :pubkey} m]
    (log/info :update-pubkey!/parsed
              {:req-id     req-id
               :tags       tags
               :id         id
               :created-at created-at
               :pubkey     pubkey-hex
               :kind       kind
               :sig        sig
               :content    content})
    (let [event-id (if-let [existing-event (q.n.events/find-by-note-id id)]
                     (do
                       (log/info :update-pubkey!/existing {:existing-event existing-event})
                       existing-event)
                     (let [pubkey-id (q.n.pubkeys/find-by-hex pubkey-hex)]
                       (log/info :update-pubkey!/missing {})
                       (q.n.events/register-event!
                        {::m.n.events/note-id    id
                         ::m.n.events/pubkey     pubkey-id
                         ::m.n.events/kind       kind
                         ::m.n.events/sig        sig
                         ::m.n.events/content    content
                         ::m.n.events/created    (ds/ms->inst (* created-at 1000))
                         ::m.n.events/created-at created-at})))]
      (doseq [[idx tag] (map-indexed vector tags)]
        (log/info :update-event!/tag {:tag tag :event-id event-id})
        (a.n.event-tags/register-tag! event-id tag idx)))))

(>defn fetch-by-note-id
  ([note-id]
   [::m.n.events/note-id => any?]
   (log/info :fetch-by-note-id/starting {:note-id note-id})
   (let [relay-ids (q.n.relays/index-ids)]
     (doseq [relay-id relay-ids]
       (fetch-by-note-id note-id relay-id))))
  ([note-id relay-id]
   [::m.n.events/note-id ::m.n.relays/id => any?]
   (log/info :fetch-by-note-id/starting {:note-id note-id :relay-id relay-id})
   (let [body {:ids [note-id]}
         c    (a.n.relays/send! relay-id body)]
     (async/go-loop []
       (if-let [m (async/<! c)]
         (do
           (log/info :fetch-event!/received {:m m})
           (update-event! m)
           (recur))
         (do
           (log/info :fetch-event!/empty {})
           nil))))))

(>defn fetch-event!
  [event-id]
  [::m.n.events/id => any?]
  (log/info :fetch-event!/starting {:event-id event-id})
  (if-let [event (q.n.events/read-record event-id)]
    (let [note-id (::m.n.events/note-id event)]
      (log/info :fetch-event!/fetched {:event event})
      (fetch-by-note-id note-id))
    (throw (ex-info "Failed to find event" {}))))

(s/def ::id (s/tuple #{"id"} string?))
(s/def ::content (s/tuple #{"content"} string?))
(s/def ::pubkey (s/tuple #{"pubkey"} string?))
(s/def ::sig (s/tuple #{"sig"} string?))
(s/def ::created-at (s/tuple #{"created_at"} int?))
(s/def ::kind (s/tuple #{"kind"} int?))
(s/def ::tags (s/tuple #{"tags"} vector?))

(s/def ::event-body
  (s/coll-of
   (s/or :id ::id
         :pubkey ::pubkey
         :created-at ::created-at
         :kind ::kind
         :content ::content
         :sig ::sig
         :tag ::tags)
   :kind map?))

(defn register-remote-event!
  [note-id relay]
  (if-let [event-id (q.n.events/find-by-note-id note-id)]
    event-id
    (let [relay-id (a.n.relays/register-relay! relay)]
      (comment relay-id)
      nil)))

(>defn register-tag!
  [event-id tag idx]
  [::m.n.events/id any? number? => any?]
  (log/info :register-tag!/start {:event-id event-id :tag tag})
  (let [[key value relay extra] tag]
    (condp = key
      "p"
      (if-let [pubkey-id (a.n.pubkeys/register-pubkey! value)]
        (q.n.event-tags/create-record
         {::m.n.event-tags/index  idx
          ::m.n.event-tags/parent event-id
          ::m.n.event-tags/pubkey pubkey-id
          ::m.n.event-tags/relay  relay
          ::m.n.event-tags/extra  extra})
        (throw (ex-info "Failed to find pubkey" {})))

      "e"
      (if-let [target-id (register-remote-event! value relay)]
        (q.n.event-tags/create-record
         {::m.n.event-tags/index  idx
          ::m.n.event-tags/parent event-id
          ::m.n.event-tags/event  target-id
          ::m.n.event-tags/relay  relay
          ::m.n.event-tags/extra  extra})
        (q.n.event-tags/create-record
         {::m.n.event-tags/index  idx
          ::m.n.event-tags/parent event-id
          ::m.n.event-tags/note-id  value
          ;; ::m.n.event-tags/event  target-id
          ::m.n.event-tags/relay  relay
          ::m.n.event-tags/extra  extra})

        ;; (throw (ex-info "Failed to find note" {}))
        )
      (throw (ex-info "unknown key" {})))))

(>defn register-event!
  [msg]
  [::a.n.connections/outgoing-event => ::m.n.events/id]
  (let [{note-id    :id
         pubkey-hex :pubkey
         created-at :created-at
         kind       :kind
         content    :content
         sig        :sig
         tags       :tags} msg]
    (if-let [event-id (q.n.events/find-by-note-id note-id)]
      (do
        (log/info :register-event!/found {:event-id event-id})
        event-id)
      (do
        (log/info :register-event!/not-found {})
        (let [pubkey-id (a.n.pubkeys/register-pubkey! pubkey-hex)
              event-id  (q.n.events/create-record
                         {::m.n.events/note-id    note-id
                          ::m.n.events/pubkey     pubkey-id
                          ::m.n.events/created-at created-at
                          ::m.n.events/kind       kind
                          ::m.n.events/content    content
                          ::m.n.events/sig        sig})]
          (log/info :register-event!/created {:event-id event-id})
          (doseq [[idx tag] (map-indexed vector tags)]
            (log/info :register-event!/tag {:tag tag})
            (a.n.event-tags/register-tag! event-id tag idx))
          event-id)))))

(defn extract-urls [text]
  (map first (re-seq #"(?i)\b((?:https?://|www\d{0,3}[.]|[a-z0-9.\-]+[.][a-z]{2,4}/)(?:[^\s()<>]+|\(([^\s()<>]+|(\([^\s()<>]+\)))*\))+(?:\(([^\s()<>]+|(\([^\s()<>]+\)))*\)|[^\s`!()\[\]{};:'\".,<>?«»“”‘’]))" text)))

(comment

  (q.n.pubkeys/index-ids)

  (def alice-id (first (q.n.pubkeys/find-by-name "alice")))
  (def duck-id (first (q.n.pubkeys/find-by-name "duck")))

  (fetch-by-note-id "e4f5b8f980885e5f013d1b0549ce871c42d892e744da3e4a611a65202a227472")
  (fetch-by-note-id "36df49af7fe181520beee31644f121ea2bb8e4ff99468d08f56040e5b792bea5")

  (def event (q.n.events/read-record (new-uuid "0186ae78-ae3d-8ab5-8af2-907aa8716e04")))
  event

  (def relay-id (first (q.n.relays/index-ids)))
  relay-id

  (fetch-by-note-id (::m.n.events/note-id event) relay-id)

  (q.n.pubkeys/read-record alice-id)
  (q.n.pubkeys/read-record duck-id)

  (q.n.events/count-ids)

  (q.n.events/find-by-author duck-id)
  (q.n.events/find-by-author alice-id)

  (map q.n.events/read-record (q.n.events/index-ids))

  (def message "👀 https://nostr.build/i/nostr.build_9e6becea72a9673f6e33ade5fa7961728fb3758df5d56e376acb89f10e1c242e.jpeg https://nostr.build/i/nostr.build_fb5377f37c0dcedf5c88507b157b513c3ae75839fd43e5d6faa237c0b9f0d6e3.jpeg")

  (def dperini-matcher
    #"(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z0-9\u00a1-\uffff][a-z0-9\u00a1-\uffff_-]{0,62})?[a-z0-9\u00a1-\uffff]\.)+(?:[a-z\u00a1-\uffff]{2,}\.?))(?::\d{2,5})?(?:[/?#]\S*)?")

  (re-find dperini-matcher message)

  (re-find #"https?:\/\/[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)" message)

  (extract-urls message)

  nil)
