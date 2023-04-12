(ns dinsro.components.xtdb
  (:require
   [dinsro.components.config :refer [config]]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [xtdb.api :as c.api]))

(declare xtdb-nodes)

(defn start-database!
  "Start the xtdb database"
  []
  (let [conf (xt/symbolize-xtdb-modules config)]
    (log/finest :db/starting {:conf conf})
    (let [node (xt/start-databases conf)]
      (log/trace :db/started {:conf conf :node node})
      node)))

(defn stop-database!
  "Start the xtdb database"
  []
  (for [node xtdb-nodes]
    (.close node)))

(defstate ^{:on-reload :noop} xtdb-nodes
  "A collection of started xtdb nodes"
  :start (start-database!)
  :stop (stop-database!))

(defn main-node
  "Returns the main xtdb node"
  []
  #_(log/trace :nodes/read {:nodes xtdb-nodes})
  (:main xtdb-nodes))

(defn main-db
  "Returns the main xtdb database"
  []
  (let [node (main-node)
        db (c.api/db node)]
    #_(log/trace :db/read {:db db :node node})
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

(defn submit-tx!
  [k params]
  (let [ops      [(concat [::c.api/fn k] params)]]
    (log/trace :submit-tx/starting {:ops ops})
    (let [node     (main-node)
          response (c.api/submit-tx node ops)]
      (log/trace :submit-tx!/finished {:response response})
      response)))
