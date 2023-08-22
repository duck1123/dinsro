(ns dinsro.responses.ln.payments
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.mutations :as mu]))

;; [[../../mutations/ln/payments.cljc]]

(def model-key ::m.ln.payments/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
