(ns dinsro.responses.currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.mutations :as mu]))

;; [../actions/currencies.clj]
;; [../mutations/currencies.cljc]
;; [../processors/currencies.clj]

(defsc DeleteResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::deleted-records]})
