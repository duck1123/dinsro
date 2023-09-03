(ns dinsro.responses.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.mutations :as mu]
   [dinsro.options.accounts :as o.accounts]))

;; [[../options/accounts.cljc]]
;; [[../processors/accounts.clj]]

(def model-key o.accounts/id)

(defsc CreateResponse
  [_this _props]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(s/def ::deleted-records (s/coll-of model-key))
(s/def ::delete!-request (s/keys :req [model-key]))
(s/def ::delete!-response (s/keys :opt [::mu/errors ::mu/status ::deleted-records]))
