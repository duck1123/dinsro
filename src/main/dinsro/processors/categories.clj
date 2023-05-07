(ns dinsro.processors.categories
  (:require
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  {::mu/status :ok})