(ns dinsro.actions.instances
  (:require
   [dinsro.queries.instances :as q.instances]
   [lambdaisland.glogc :as log]
   [manifold.time :as mt]
   [mount.core :as mount]))

;; [[../joins/instances.cljc]]
;; [[../model/instances.cljc]]
;; [[../queries/instances.clj]]
;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(defonce ^:dynamic *current-instance-id* (atom nil))
(declare ^:dynamic *scheduler*)
(def scheduler-enabled true)
(def schedule-rate (mt/minutes 5))

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
  (if-let [instance-id @*current-instance-id*]
    instance-id
    (let [created-id (create! {})]
      (log/info :register/created {:created-id created-id})
      (reset! *current-instance-id* created-id)
      created-id)))

(defn heartbeat!
  []
  (beat! (register!)))

(defn start-scheduler!
  []
  (log/info :start-scheduler!/starting {:enabled scheduler-enabled})
  (let [scheduler (when scheduler-enabled (mt/every schedule-rate #'heartbeat!))]
    (log/info :start-scheduler!/finished {:scheduler scheduler})
    scheduler))

(defn stop-scheduler!
  []
  (log/info :stop-scheduler!/starting {})
  (when-let [stop! @*scheduler*]
    (stop!))
  (log/info :stop-scheduler!/finished {})
  nil)

(mount/defstate ^:dynamic *scheduler*
  :start (start-scheduler!)
  :stop (stop-scheduler!))
