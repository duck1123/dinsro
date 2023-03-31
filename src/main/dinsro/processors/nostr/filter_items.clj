(ns dinsro.processors.nostr.filter-items
  (:require
   [lambdaisland.glogc :as log]))

(defn delete!
  [props]
  (log/info :delete!/starting {:props props}))
