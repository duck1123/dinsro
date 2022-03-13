(ns dinsro.components.xtdb
  (:require
   [dinsro.components.config :refer [config]]
   [mount.core :refer [defstate]]
   [lambdaisland.glogc :as log]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [xtdb.api :as c.api]))

(declare xtdb-nodes)

(defn start-database!
  "Start the xtdb database"
  []
  (let [conf (xt/symbolize-xtdb-modules config)]
    (log/finest :db/starting {:conf conf})
    (let [node (xt/start-databases conf)]
      (log/finer :db/started {:conf conf :node node})
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
  (log/finer :nodes/read {:nodes xtdb-nodes})
  (:main xtdb-nodes))

(defn main-db
  "Returns the main xtdb database"
  []
  (let [node (main-node)
        db (c.api/db node)]
    (log/finer :db/read {:db db :node node})
    db))
