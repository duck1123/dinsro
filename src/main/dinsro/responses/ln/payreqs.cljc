(ns dinsro.responses.ln.payreqs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.mutations :as mu]))

;; [[../../mutations/ln/payreqs.cljc]]

(def model-key ::m.ln.payreqs/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
