(ns dinsro.queries.instances
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.instances :as m.instances]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]
   [tick.alpha.api :as t]
   [xtdb.api :as xt]))

;; [[../actions/instances.clj]]
;; [[../joins/instances.cljc]]
;; [[../model/instances.cljc]]
;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(def model-key ::m.instances/id)

(def instance-timeout-minutes
  "The amount of time (in minutes) an instance can go without updating its heartbeat without being considered stale"
  15)

(>defn expired?
  [time]
  [ds/instance? => boolean?]
  (let [now (ds/->inst)]
    (t/<
     (t/instant time)
     (t/<< now (t/new-duration instance-timeout-minutes :minutes)))))

(def query-info
  {:ident        model-key
   :pk           '?instance-id
   :clauses      [[:actor/id     '?actor-id]
                  [:actor/admin? '?admin?]
                  [::m.users/id  '?user-id]
                  [:expired?     '?expired]]
   :order-by [['?instance-id :desc]]
   :sort-columns {}
   :rules
   (fn [[_actor-id _admin? _user-id expired?] rules]
     (->> rules
          (concat-when expired?
            [['?instance-id ::m.instances/last-heartbeat '?expired-heartbeat]])))})

(defn count-ids
  "Count instance records"
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  "Index instance records"
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.instances/params => :xt/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        time            (ds/->inst)
        prepared-params (-> params
                            (assoc ::m.instances/id id)
                            (assoc ::m.instances/created-time time)
                            (assoc ::m.instances/last-heartbeat time)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.instances/id => (? ::m.instances/item)]
  (c.xtdb/read model-key id))

(defn delete!
  [id]
  (c.xtdb/delete! id))

(defn beat!
  [id]
  (log/debug :beat!/starting {:id id})
  (c.xtdb/submit-tx! ::beat! [id]))

(defn create-beat!
  []
  (let [node (c.xtdb/get-node)
        query-def
        {:xt/id ::beat!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity {::m.instances/last-heartbeat time})]
                     [[::xt/put updated-entity]]))}]
    (xt/await-tx node (xt/submit-tx node [[::xt/put query-def]]))))

(defn initialize-queries!
  []
  (log/debug :initialize-queries!/starting {})
  (create-beat!)
  (log/trace :initialize-queries!/finished {}))
