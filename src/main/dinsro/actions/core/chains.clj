(ns dinsro.actions.core.chains
  (:require
   [dinsro.queries.core.chains :as q.c.chains]
   [lambdaisland.glogc :as log]))

(defn delete!
  [id]
  (log/info :delete!/staring {:id id})
  (q.c.chains/delete! id))
