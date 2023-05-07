(ns dinsro.queries.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
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

;; [[../../actions/nostr/relays.clj][Actions]]
;; [[../nostr.clj][Nostr Queries]]

(>defn create-record
  "Create a relay record"
  [params]
  [::m.n.relays/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [connected       (boolean (::m.n.relays/connected params))
        node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.relays/connected connected)
                            (assoc ::m.n.relays/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  "Read a relay record"
  [id]
  [::m.n.relays/id => (? ::m.n.relays/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.relays/id)
      (dissoc record :xt/id))))

(defn get-index-params
  [query-params]
  (let [{connection-id ::m.n.connections} query-params]
    [connection-id]))

(defn get-index-query
  [query-params]
  (let [[connection-id] (get-index-params query-params)]
    {:find  ['?relay-id]
     :in    [['?connection-id]]
     :where (->> [['?relay-id ::m.n.relays/id '_]]
                 (concat (when connection-id
                           [['?connection-id ::m.n.connections/relay '?relay-id]]))
                 (filter identity)
                 (into []))}))

(>defn count-ids
  ([]
   [=> number?]
   (count-ids {}))
  ([query-params]
   [map? => number?]
   (do
     (log/info :count-ids/starting {:query-params query-params})
     (let [base-query  (get-index-query query-params)
           limit-query {:find ['(count ?relay-id)]}
           query       (merge base-query limit-query)
           params      (get-index-params query-params)]
       (log/info :count-ids/query {:query query :params params})
       (let [n (c.xtdb/query-one query params)]
         (log/trace :count-ids/finished {:n n})
         n)))))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.n.relays/id)]
   (index-ids {}))
  ([query-params]
   [map? => (s/coll-of ::m.n.relays/id)]
   (do
     (log/trace :index-ids/starting {})
     (let [base-query                       (get-index-query query-params)
           {:indexed-access/keys [options]} query-params
           {:keys [limit offset]
            :or   {limit 20 offset 0}}      options
           limit-query                      {:limit limit :offset offset}
           query                            (merge base-query limit-query)
           params                           []]
       (log/info :index-ids/query {:query query :params params})
       (let [ids (c.xtdb/query-ids query params)]
         (log/trace :index-ids/finished {:ids ids})
         ids)))))

(>defn find-by-address
  [address]
  [::m.n.relays/address => (? ::m.n.relays/id)]
  (log/info :find-by-address/starting {:address address})
  (c.xtdb/query-id
   '{:find  [?relay-id]
     :in    [[?address]]
     :where [[?relay-id ::m.n.relays/address ?address]]}
   [address]))

(>defn delete-record
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)] (delete-record id)))

(>defn register-relay
  [address]
  [string? => ::m.n.relays/id]
  (log/info :register-relay/starting {:address address})
  (if-let [id (find-by-address address)]
    id
    (do
      (log/info :register-relay/not-found {})
      (create-record {::m.n.relays/address address}))))

(defn create-connected-toggle
  []
  (log/info :create-connected-toggle/starting {})
  (let [toggle-connected {:xt/id ::toggle-connected
                          :xt/fn '(fn [ctx eid connected]
                                    (let [db           (xtdb.api/db ctx)
                                          entity       (xtdb.api/entity db eid)
                                          updated-data (assoc entity ::m.n.relays/connected connected)]
                                      [[::xt/put updated-data]]))}
        node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put toggle-connected]]))))

(>defn set-connected
  [relay-id connected]
  [::m.n.relays/id ::m.n.relays/connected => any?]
  (log/info :set-connected/starting {:relay-id relay-id :connected connected})
  (let [node     (c.xtdb/main-node)
        response (xt/submit-tx node [[::xt/fn ::toggle-connected relay-id connected]])]
    (log/trace :set-connected/finished {:response response})
    response))

(defn initialize-queries!
  []
  (log/info :initialize-queries!/starting {})
  (create-connected-toggle)
  (log/info :initialize-queries!/finished {}))

(defn find-by-connection
  [connection-id]
  (log/debug :find-by-connection/starting {:connection-id connection-id})
  (let [id (c.xtdb/query-id
            '{:find  [?relay-id]
              :in    [[?connection-id]]
              :where [[?connection-id ::m.n.connections/relay ?relay-id]]}
            [connection-id])]
    (log/trace :find-by-connection/finished {:id id})
    id))

(defn find-by-filter-item
  [filter-item-id]
  (log/debug :find-by-request/starting {:filter-item-id filter-item-id})
  (let [id (c.xtdb/query-id
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
  (let [id (c.xtdb/query-id
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
  (let [id (c.xtdb/query-id
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
        id     (c.xtdb/query-id query params)]
    (log/trace :find-by-request/finished {:id id})
    id))

(comment

  (some->
   (index-ids)
   first
   read-record)

  (create-record
   {::m.n.relays/addresses "wss://relay.kronkltd.net/"})

  (find-by-address "wss://relay.kronkltd.net/")

  (register-relay "wss://relay.kronkltd.net/")

  (delete-all)

  nil)
