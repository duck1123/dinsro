(ns dinsro.responses.instances
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.instances :as m.instances]
   [dinsro.mutations :as mu]))

;; [[../actions/instances.clj]]
;; [[../model/instances.cljc]]
;; [[../queries/instances.clj]]
;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(defsc BeatResponse
  [_this _props]
  {:initial-state {::item []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::item [::m.instances/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.instances/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
