(ns dinsro.actions.core.connections
  (:require
   [dinsro.queries.core.connections :as q.c.connections]
   [lambdaisland.glogc :as log]))

(defn create!
  [props]
  (log/info :connections/creating {:props props}))

(comment

  (q.c.connections/index-ids)

  nil)
