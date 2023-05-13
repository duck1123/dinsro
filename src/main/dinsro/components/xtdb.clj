(ns dinsro.components.xtdb
  (:require
   [dinsro.components.config :refer [get-config]]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [xtdb.api :as c.api]))

(declare xtdb-nodes)
(def db-key :main)

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

(defn get-node
  "Returns the main xtdb node"
  ([] (get-node db-key))
  ([key]
   (let [nodes @xtdb-nodes]
     (log/trace :nodes/read {:nodes nodes})
     (key nodes))))

(defn get-db
  "Returns the main xtdb database"
  ([] (get-db db-key))
  ([key]
   (let [node (get-node key)
         db   (c.api/db node)]
     (log/trace :db/read {:db db :node node})
     db)))

(defn submit-tx!
  "Submit a transaction to the main db"
  [k params]
  (let [ops [(concat [::c.api/fn k] params)]]
    (log/trace :submit-tx/starting {:ops ops})
    (let [node     (get-node)
          response (c.api/submit-tx node ops)]
      (log/trace :submit-tx!/finished {:response response})
      response)))

(defn run-query
  "Run a query against the main database"
  ([query]
   [query]
   (log/trace :run-query/starting {:query query})
   (let [db      (get-db)
         results (c.api/q db query)]
     (log/trace :run-query/finished {:results results})
     results))
  ([query params]
   (log/trace :run-query/starting {:query query :params params})
   (let [db      (get-db)
         results (c.api/q db query params)]
     (log/trace :run-query/finished {:results results})
     results)))

(defn query-value
  "Run a query to produce a single value"
  ([query]
   (log/trace :query-value/starting {:query query})
   (let [results (run-query query)
         id      (ffirst results)]
     (log/trace :query-value/finished {:id id :results results})
     id))
  ([query params]
   (log/trace :query-value/starting {:query query :params params})
   (let [results (run-query query params)
         id      (ffirst results)]
     (log/trace :query-value/finished {:id id :results results})
     id)))

(defn query-values
  "Run a query to produce a sequence of single values"
  ([query]
   (log/trace :query-values/starting {:query query})
   (let [ids (map first (run-query query))]
     (log/trace :query-values/finished {:ids ids})
     ids))
  ([query params]
   (log/trace :query-values/starting {:query query :params params})
   (let [ids (map first (run-query query params))]
     (log/trace :query-values/finished {:ids ids})
     ids)))
