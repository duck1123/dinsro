(ns dinsro.components.datomic
  (:require
   [com.fulcrologic.rad.database-adapters.datomic :as datomic]
   [mount.core :refer [defstate]]
   [dinsro.model :refer [all-attributes]]
   [dinsro.components.config :refer [config]]))

(defstate ^{:on-reload :noop} datomic-connections
  :start
  (datomic/start-databases all-attributes config))
