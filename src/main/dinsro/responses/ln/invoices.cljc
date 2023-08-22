(ns dinsro.responses.ln.invoices
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.mutations :as mu]))

;; [[../../mutations/ln/invoices.cljc]]

(def model-key ::m.ln.invoices/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
