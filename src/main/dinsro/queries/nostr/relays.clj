(ns dinsro.queries.nostr.relays
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/relays.clj]]
;; [[../../queries.clj]]
;; [[../../ui/nostr/relays.cljc]]

(def model-key ::m.n.relays/id)

(def query-info
  {:ident   model-key
   :pk      '?relay-id
   :clauses [[::m.n.connections/id '?connection-id]]
   :rules
   (fn [[connection-id] rules]
     (->> rules
          (concat-when connection-id
            [['?connection-id ::m.n.connections/relay '?relay-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  "Create a relay record"
  [params]
  [::m.n.relays/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [connected       (boolean (::m.n.relays/connected params))
        node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.relays/connected connected)
                            (assoc model-key id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  "Read a relay record"
  [id]
  [::m.n.relays/id => (? ::m.n.relays/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (model-key record)
      (dissoc record :xt/id))))

(>defn find-by-address
  [address]
  [::m.n.relays/address => (? ::m.n.relays/id)]
  (log/info :find-by-address/starting {:address address})
  (c.xtdb/query-value
   '{:find  [?relay-id]
     :in    [[?address]]
     :where [[?relay-id ::m.n.relays/address ?address]]}
   [address]))

(>defn delete!
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)] (delete! id)))

(>defn register-relay
  [address]
  [string? => ::m.n.relays/id]
  (log/info :register-relay/starting {:address address})
  (if-let [id (find-by-address address)]
    id
    (do
      (log/info :register-relay/not-found {})
      (create-record {::m.n.relays/address address}))))

(defn find-by-connection
  [connection-id]
  (log/debug :find-by-connection/starting {:connection-id connection-id})
  (let [id (c.xtdb/query-value
            '{:find  [?relay-id]
              :in    [[?connection-id]]
              :where [[?connection-id ::m.n.connections/relay ?relay-id]]}
            [connection-id])]
    (log/trace :find-by-connection/finished {:id id})
    id))

(defn find-by-filter-item
  [filter-item-id]
  (log/debug :find-by-request/starting {:filter-item-id filter-item-id})
  (let [id (c.xtdb/query-value
            '{:find  [?relay-id]
              :in    [[?filter-item-id]]
              :where [[?filter-item-id ::m.n.filter-items/filter ?filter-id]
                      [?filter-id ::m.n.filters/request ?request-id]
                      [?request-id ::m.n.requests/relay ?relay-id]]}

            [filter-item-id])]
    (log/trace :find-by-request/finished {:id id})
    id))

(defn find-by-request
  [request-id]
  (log/debug :find-by-request/starting {:request-id request-id})
  (let [id (c.xtdb/query-value
            '{:find  [?relay-id]
              :in    [[?request-id]]
              :where [[?request-id ::m.n.requests/relay ?relay-id]]}
            [request-id])]
    (log/trace :find-by-request/finished {:id id})
    id))

(>defn find-by-run
  [run-id]
  [::m.n.runs/id => (? ::m.n.relays/id)]
  (log/debug :find-by-run/starting {:run-id run-id})
  (let [id (c.xtdb/query-value
            '{:find  [?relay-id]
              :in    [[?run-id]]
              :where [[?run-id ::m.n.runs/request ?request-id]
                      [?request-id ::m.n.requests/relay ?relay-id]]}
            [run-id])]
    (log/trace :find-by-request/finished {:id id})
    id))

(>defn find-by-witness
  [witness-id]
  [::m.n.witnesses/id => (? ::m.n.relays/id)]
  (log/debug :find-by-witness/starting {:witness-id witness-id})
  (let [query  '{:find  [?relay-id]
                 :in    [[?witness-id]]
                 :where [[?witness-id ::m.n.witnesses/run ?run-id]
                         [?run-id ::m.n.runs/request ?request-id]
                         [?request-id ::m.n.requests/relay ?relay-id]]}
        params [witness-id]
        id     (c.xtdb/query-value query params)]
    (log/trace :find-by-request/finished {:id id})
    id))
