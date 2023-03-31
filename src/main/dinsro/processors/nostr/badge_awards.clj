(ns dinsro.processors.nostr.badge-awards
  (:require
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

(defn fetch!
  [props]
  (log/info :fetch!/starting {:props props})
  (mu/error-response "Not Implemented"))
