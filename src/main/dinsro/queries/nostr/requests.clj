(ns dinsro.queries.nostr.requests
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.n.requests/id
   :pk      '?requests-id
   :clauses [[::m.n.relays/id '?relay-id]]
   :rules
   (fn [[relay-id] rules]
     (->> rules
          (concat-when relay-id
            ['?request-id ::m.n.requests/relay '?relay-id])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.requests/params => ::m.n.requests/id]
  (log/debug :create-record/starting {:params params})
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (merge
                         {::m.n.requests/id id
                          :xt/id            id}
                         params)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.n.requests/id => (? ::m.n.requests/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (log/debug :read-record/starting {:record record})
    (when (get record ::m.n.requests/id)
      (dissoc record :xt/id))))

(>defn find-by-relay
  "Find all requests for relay"
  [relay-id]
  [::m.n.relays/id => (s/coll-of ::m.n.requests/id)]
  (log/debug :find-by-relay/starting {:relay-id relay-id})
  (let [ids (c.xtdb/query-values
             '{:find  [?id]
               :in    [[?relay-id]]
               :where [[?id ::m.n.requests/relay ?relay-id]]}
             [relay-id])]
    (log/trace :find-by-relay/finished {:ids ids})
    ids))

(>defn find-by-code
  [code]
  [::m.n.requests/code => (s/coll-of ::m.n.requests/id)]
  (log/debug :find-by-relay-and-code/starting {:code code})
  (let [id (c.xtdb/query-values
            '{:find  [?request-id]
              :in    [[?code]]
              :where [[?request-id ::m.n.requests/code ?code]]}
            [code])]
    (log/trace :find-by-relay-and-code/finished {:id id})
    id))

(>defn find-by-relay-and-code
  "Find request for relay with code"
  [relay-id code]
  [::m.n.relays/id  ::m.n.requests/code => (? ::m.n.requests/id)]
  (log/debug :find-by-relay-and-code/starting {:relay-id relay-id :code code})
  (let [id (c.xtdb/query-value
            '{:find  [?request-id]
              :in    [[?relay-id ?code]]
              :where [[?request-id ::m.n.requests/relay ?relay-id]
                      [?request-id ::m.n.requests/code ?code]]}
            [relay-id code])]
    (log/trace :find-by-relay-and-code/finished {:id id})
    id))

(defn find-by-connection-and-code
  [connection-id code]
  (log/debug :find-by-connection-and-code/starting {:connection-id connection-id :code code})
  (let [id (c.xtdb/query-value
            '{:find  [?request-id]
              :in    [[?connection-id ?code]]
              :where [[?request-id ::m.n.requests/relay ?relay-id]
                      [?request-id ::m.n.requests/code ?code]
                      [?connnection-id ::m.n.connections/relay ?relay-id]]}
            [connection-id code])]
    (log/trace :find-by-connection-and-code/finished {:id id})
    id))

(>defn find-by-filter-item
  [filter-item-id]
  [::m.n.filter-items/id => (? ::m.n.requests/id)]
  (log/debug :find-by-filter-item/starting {:filter-item-id filter-item-id})
  (let [id (c.xtdb/query-value
            '{:find  [?request-id]
              :in    [[?filter-item-id]]
              :where [[?filter-id ::m.n.filters/request ?request-id]
                      [?filter-item-id ::m.n.filter-items/filter ?filter-id]]}
            [filter-item-id])]
    (log/trace :find-filter-item/finished {:id id})
    id))

(>defn find-by-run
  [run-id]
  [::m.n.runs/id => (? ::m.n.requests/id)]
  (log/debug :find-by-run/starting {:run-id run-id})
  (let [id (c.xtdb/query-value
            '{:find  [?request-id]
              :in    [[?run-id]]
              :where [[?run-id ::m.n.runs/request ?request-id]]}
            [run-id])]
    (log/trace :find-by-run/finished {:id id})
    id))

(>defn find-code-by-run
  [run-id]
  [::m.n.runs/id => (? string?)]
  (log/debug :find-code-by-run/starting {:run-id run-id})
  (let [id (c.xtdb/query-value
            '{:find  [?code]
              :in    [[?run-id]]
              :where [[?run-id ::m.n.runs/request ?request-id]
                      [?request-id ::m.n.requests/code ?code]]}
            [run-id])]
    (log/finer :find-code-by-run/finished {:id id})
    id))

(>defn delete!
  "Delete request by id"
  [id]
  [::m.n.requests/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all!
  "Delete all requests"
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))

(>defn find-relay
  "Find the relay id for the request"
  [request-id]
  [::m.n.requests/id => (? ::m.n.requests/relay)]
  (log/debug :find-relay/starting {:request-id request-id})
  (let [id (c.xtdb/query-value
            '{:find  [?relay-id]
              :in    [[?request-id]]
              :where [[?request-id ::m.n.requests/relay ?relay-id]]}
            [request-id])]
    (log/trace :find-relay/finished {:id id})
    id))
