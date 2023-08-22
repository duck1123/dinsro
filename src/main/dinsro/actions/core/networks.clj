(ns dinsro.actions.core.networks
  (:require
   [dinsro.queries.core.networks :as q.c.networks]
   [lambdaisland.glogc :as log]))

(defn delete!
  [id]
  (log/info :delete!/staring {:id id})
  (q.c.networks/delete! id))
