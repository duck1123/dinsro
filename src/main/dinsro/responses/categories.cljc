(ns dinsro.responses.categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.categories :as m.categories]
   [dinsro.mutations :as mu]))

;; [../model/categories.cljc]
;; [../mutations/categories.cljc]
;; [../ui/categories.cljs]
;; [../ui/admin/categories.cljs]
;; [../ui/admin/users/categories.cljs]

(defsc CreateResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   {::created-record [::m.categories/id]}]})

(defsc DeleteResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::deleted-records]})
