(ns dinsro.components.crux
  (:require
   [crux.api :as c.api]
   [roterski.fulcro.rad.database-adapters.crux :as crux]
   [mount.core :refer [defstate]]
   [dinsro.components.config :refer [config]]))

(defstate ^{:on-reload :noop} crux-nodes
  :start
  (crux/start-databases (crux/symbolize-crux-modules config))
  :stop
  (for [node crux-nodes]
    (.close node)))

(defn main-node
  []
  (:main crux-nodes))

(defn main-db
  []
  (c.api/db (main-node)))
