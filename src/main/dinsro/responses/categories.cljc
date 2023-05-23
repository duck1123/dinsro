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
  {:initial-state {::created-record []
                   ::mu/status      :initial
                   ::mu/errors      {}}
   :query         [{::created-record [::m.categories/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(defsc DeleteResponse
  [_ _]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.categories/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
