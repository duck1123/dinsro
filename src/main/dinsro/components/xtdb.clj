(ns dinsro.components.xtdb
  (:require
   [xtdb.api :as c.api]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [mount.core :refer [defstate]]
   [dinsro.components.config :refer [config]]))

(defstate ^{:on-reload :noop} xtdb-nodes
  :start
  (xt/start-databases (xt/symbolize-xtdb-modules config))
  :stop
  (for [node xtdb-nodes]
    (.close node)))

(defn main-node
  []
  (:main xtdb-nodes))

(defn main-db
  []
  (c.api/db (main-node)))
