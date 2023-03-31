(ns dinsro.processors.nostr.badge-definitions
  (:require
   [lambdaisland.glogc :as log]))

(defn do-fetch-definitions!
  [props]
  (log/info :do-fetch-definitions!/starting {:props props}))
