(ns dinsro.responses.currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mu]))

;; [../actions/currencies.clj]
;; [../mutations/currencies.cljc]
;; [../processors/currencies.clj]

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.currencies/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
