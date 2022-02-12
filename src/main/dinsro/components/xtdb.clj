(ns dinsro.components.xtdb
  (:require
   [xtdb.api :as c.api]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [mount.core :refer [defstate]]
   [dinsro.components.config :refer [config]]))

(declare xtdb-nodes)

(defn start-database!
  "Start the xtdb database"
  []
  (xt/start-databases (xt/symbolize-xtdb-modules config)))

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
  (:main xtdb-nodes))

(defn main-db
  "Returns the main xtdb database"
  []
  (c.api/db (main-node)))
