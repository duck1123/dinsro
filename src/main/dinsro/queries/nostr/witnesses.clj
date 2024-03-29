(ns dinsro.queries.nostr.witnesses
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/witnesses.clj]]
;; [[../../joins/nostr/witnesses.cljc]]
;; [[../../ui/nostr/events/witnesses.cljs]]
;; [[../../ui/nostr/relays/witnesses.cljs]]

(def model-key ::m.n.witnesses/id)

(def query-info
  {:ident   model-key
   :pk      '?witness-id
   :clauses [[::m.n.connections/id '?connection-id]
             [::m.n.events/id      '?event-id]
             [::m.n.pubkeys/id     '?pubkey-id]
             [::m.n.relays/id      '?relay-id]
             [::m.n.runs/id        '?run-id]]
   :rules
   (fn [[connection-id event-id pubkey-id relay-id run-id] rules]
     (->> rules
          (concat-when event-id
            [['?witness-id          ::m.n.witnesses/event   '?event-id]])
          (concat-when pubkey-id
            [['?witness-id          ::m.n.witnesses/event   '?pubkey-event-id]
             ['?pubkey-event-id     ::m.n.events/pubkey     '?pubkey-id]])
          (concat-when run-id
            [['?witness-id          ::m.n.witnesses/run     '?run-id]])
          (concat-when connection-id
            [['?witness-id          ::m.n.witnesses/run     '?connection-run-id]
             ['?connection-run-id   ::m.n.runs/connection   '?relay-connection-id]])
          (concat-when relay-id
            [['?witness-id          ::m.n.witnesses/run     '?relay-run-id]
             ['?relay-run-id        ::m.n.runs/connection   '?relay-connection-id]
             ['?relay-connection-id ::m.n.connections/relay '?relay-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.witnesses/params => ::m.n.witnesses/id]
  (log/debug :create-record/starting {:params params})
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.n.witnesses/id => (? ::m.n.witnesses/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [::m.n.witnesses/id => nil?]
  (c.xtdb/delete! id))

(>defn find-by-event-and-run
  [event-id run-id]
  [::m.n.events/id  ::m.n.runs/id => (? ::m.n.witnesses/id)]
  (log/trace :find-by-event-and-run/starting {:event-id event-id :run-id run-id})
  (c.xtdb/query-value
   '{:find  [?witness-id]
     :in    [[?event-id ?run-id]]
     :where [[?witness-id ::m.n.witnesses/event ?event-id]
             [?witness-id ::m.n.witnesses/run ?run-id]]}
   [event-id run-id]))
