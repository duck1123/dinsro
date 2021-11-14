(ns dinsro.components.xtdb
  (:require
   [xtdb.api :as c.api]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [mount.core :refer [defstate]]
   [dinsro.components.config :refer [config]]
   [taoensso.timbre :as log]))

(declare xtdb-nodes)

(defn start-database!
  []
  (xt/start-databases (xt/symbolize-xtdb-modules config)))

(defn stop-database!
  []
  (for [node xtdb-nodes]
    (.close node)))

(defstate ^{:on-reload :noop} xtdb-nodes
  :start (start-database!)
  :stop (stop-database!))

(defn main-node
  []
  (:main xtdb-nodes))

(defn main-db
  []
  (c.api/db (main-node)))
