(ns dinsro.processors.nostr.witnesses
  (:require
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

(defn do-delete!
  [props]
  (log/info :do-delete!/starting {:props props})
  (mu/error-response "Not Implemented"))
