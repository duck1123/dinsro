(ns dinsro.components.xtdb
  (:require
   [dinsro.components.config :refer [get-config]]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [xtdb.api :as c.api]))

(declare xtdb-nodes)

(defn start-database!
  "Start the xtdb database"
  ([]
   (let [conf (xt/symbolize-xtdb-modules (get-config))]
     (start-database! conf)))
  ([conf]
   (log/info :start-database!/starting {:conf conf})
   (let [node (xt/start-databases conf)]
     (log/trace :start-database!/finished {:conf conf :node node})
     node)))

(defn stop-database!
  "Start the xtdb database"
  []
  (log/info :stop-database!/starting {})
  (let [nodes @xtdb-nodes]
    (for [node nodes] (.close node))))

(defstate ^{:on-reload :noop} xtdb-nodes
  "A collection of started xtdb nodes"
  :start (start-database!)
  :stop (stop-database!))

(defn main-node
  "Returns the main xtdb node"
  []
  (let [nodes @xtdb-nodes]
    (log/trace :nodes/read {:nodes nodes})
    (:main nodes)))

(defn main-db
  "Returns the main xtdb database"
  []
  (let [node (main-node)
        db   (c.api/db node)]
    (log/trace :db/read {:db db :node node})
    db))

(defn query-id
  ([query]
   (log/trace :query-id/starting {:query query})
   (let [db      (main-db)
         results (c.api/q db query)
         id      (ffirst results)]
     (log/trace :query-id/finished {:id id :results results})
     id))
  ([query params]
   (log/trace :query-id/starting {:query query :params params})
   (let [db      (main-db)
         results (c.api/q db query params)
         id      (ffirst results)]
     (log/trace :query-id/finished {:id id :results results})
     id)))

(def query-one query-id)

(defn query-ids
  ([query]
   (log/trace :query-ids/starting {:query query})
   (let [db  (main-db)
         ids (map first (c.api/q db query))]
     (log/trace :query-ids/finished {:ids ids})
     ids))
  ([query params]
   (log/trace :query-ids/starting {:query query :params params})
   (let [db  (main-db)
         ids (map first (c.api/q db query params))]
     (log/trace :query-ids/finished {:ids ids})
     ids)))

(def query-many query-ids)

(defn submit-tx!
  [k params]
  (let [ops      [(concat [::c.api/fn k] params)]]
    (log/trace :submit-tx/starting {:ops ops})
    (let [node     (main-node)
          response (c.api/submit-tx node ops)]
      (log/trace :submit-tx!/finished {:response response})
      response)))
