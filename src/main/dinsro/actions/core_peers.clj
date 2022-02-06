(ns dinsro.actions.core-peers
  (:require
   [dinsro.queries.core-peers :as q.core-peers]
   [io.pedestal.log :as log]))

(defn create!
  [props]
  (log/info :peer/creating {:props props}))

(comment

  (q.core-peers/index-ids)

  nil)
