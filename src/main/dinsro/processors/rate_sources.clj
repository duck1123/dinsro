(ns dinsro.processors.rate-sources
  (:require
   [dinsro.actions.rate-sources :as a.rate-sources]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

(defn create!
  [props]
  (log/info :create/starting {:props props})
  (a.rate-sources/create! props)
  {::mu/status :ok})
