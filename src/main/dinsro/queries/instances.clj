(ns dinsro.queries.instances
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.instances :as m.instances]
   [dinsro.specs]
   [xtdb.api :as xt]))

;; [[../actions/instances.clj]]
;; [[../joins/instances.cljc]]
;; [[../model/instances.cljc]]
;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(def model-key ::m.instances/id)

(def query-info
  {:ident model-key
   :pk '?instance-id
   :clauses []
   :sort-columns {}
   :rules (fn [[] rules]
            rules)})

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
        prepared-params (-> params
                            (assoc ::m.instances/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.instances/id => (? ::m.instances/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.instances/id)
      (dissoc record :xt/id))))

(defn delete!
  [id]
  (c.xtdb/delete! id))
