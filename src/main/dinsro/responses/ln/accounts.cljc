(ns dinsro.responses.ln.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.mutations :as mu]))

;; [[../../mutations/ln/accounts.cljc]]

(def model-key ::m.ln.accounts/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
