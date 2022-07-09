(ns dinsro.actions.core.connections
  (:require
   [lambdaisland.glogc :as log]))

(defn create!
  [props]
  (log/info :connections/creating {:props props}))
