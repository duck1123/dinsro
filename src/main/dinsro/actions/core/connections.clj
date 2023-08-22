(ns dinsro.actions.core.connections
  (:require
   [dinsro.queries.nostr.connections :as q.n.connections]
   [lambdaisland.glogc :as log]))

(defn create!
  [props]
  (log/info :connections/creating {:props props}))

(defn delete!
  [id]
  (log/info :delete!/staring {:id id})
  (q.n.connections/delete! id))
