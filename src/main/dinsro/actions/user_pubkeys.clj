(ns dinsro.actions.user-pubkeys
  (:require
   [lambdaisland.glogc :as log]))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id}))
