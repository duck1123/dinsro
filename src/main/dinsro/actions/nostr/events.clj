(ns dinsro.actions.nostr.events
  (:require
   [clojure.core.async :as async]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
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

;; [[../../model/nostr/events.cljc]]
;; [[../../queries/nostr/events.clj]]
;; [[../../ui/nostr/events.cljs]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/events_notebook.clj]]

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

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.n.events/delete! id))
