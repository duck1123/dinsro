(ns dinsro.actions.instances
  (:require
   [dinsro.queries.instances :as q.instances]
   [taoensso.timbre :as log]))

;; [[../model/instances.cljc]]
;; [[../queries/instances.clj]]
;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(defn beat!
  [id]
  (log/info :beat!/starting {:id id})
  (q.instances/beat! id))

(defn create!
  [props]
  (log/info :create/starting {:props props})
  (q.instances/create-record props))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.instances/delete! id))

(defn register!
  []
  (create! {}))
