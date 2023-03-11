(ns dinsro.actions.nostr.events
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.actions.nostr.event-tags :as a.n.event-tags]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.actions.nostr.requests :as a.n.requests]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/events.cljc][Event Model]]
;; [[../../queries/nostr/events.clj][Event Queries]]
;; [[../../ui/nostr/events.cljs][Event UI]]

(>defn fetch-events!
  [pubkey-id relay-id]
  [::m.n.pubkeys/id ::m.n.relays/id => any?]
  (log/info :fetch-events!/starting {:pubkey-id pubkey-id :relay-id relay-id})
  (let [code (a.n.relays/get-next-code!)]
    (log/info :fetch-events!/starting {:pubkey-id pubkey-id :relay-id relay-id :code code})
    (let [request-id (a.n.requests/register-request relay-id code)]
      (log/info :fetch-events!/starting {:pubkey-id pubkey-id :relay-id relay-id :code code :request-id request-id}))))

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

(comment

  (q.n.events/find-by-note-id "9cc6eacf2a4b7672dbcc18e653ade0c36c000b817886f50cb8474b28cda1fc76")

  nil)

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
    (throw (RuntimeException. "Failed to find event"))))

(defn do-fetch!
  [props]
  (log/info :do-fetch!/starting {:props props})
  (let [event-id (::m.n.events/id props)]
    (fetch-event! event-id)))

(defn do-fetch-events!
  [props]
  (log/info :do-fetch-events!/starting {:props props})
  (let [{pubkey-id ::m.n.pubkeys/id relay-id ::m.n.relays/id} props]
    (log/info :do-fetch-events!/starting {:pubkey-id pubkey-id :relay-id relay-id})
    (fetch-events! pubkey-id relay-id)
    {::mu/status :ok}))

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

  (q.n.events/find-by-author duck-id)
  (q.n.events/find-by-author alice-id)

  (map q.n.events/read-record (q.n.events/index-ids))

  nil)
