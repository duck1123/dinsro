(ns dinsro.components.portal
  (:require
   [portal.client.jvm :as p]))

(defn connect!
  "Connect to a remote portal client"
  [host port]
  (println "Connecting")
  (add-tap (partial p/submit {:host host :port port})))

(comment

  (connect! "dinsro-portal" 5678)

  (tap> "1123")

  nil)
