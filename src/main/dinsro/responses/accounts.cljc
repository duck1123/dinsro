(ns dinsro.responses.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.mutations :as mu]
   [dinsro.options.accounts :as o.accounts]))

;; [[../options/accounts.cljc]]
;; [[../processors/accounts.clj]]

(def model-key o.accounts/id)

(defsc CreateResponse
  [_this _props]
  {:initial-state {::created-records []
                   ::mu/status :initial
                   ::mu/errors {}}
   :query         [{::created-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
