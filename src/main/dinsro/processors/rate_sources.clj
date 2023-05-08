(ns dinsro.processors.rate-sources
  (:require
   [dinsro.actions.rate-sources :as a.rate-sources]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

;; [../model/rate_sources.cljc]
;; [../mutations/rate_sources.cljc]

(defn create!
  [props]
  (log/info :create/starting {:props props})
  (a.rate-sources/create! props)
  {::mu/status :ok})
