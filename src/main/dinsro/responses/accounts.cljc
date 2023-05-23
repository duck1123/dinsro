(ns dinsro.responses.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.mutations :as mu]))

;; [../processors/accounts.clj]

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.accounts/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(s/def ::deleted-records (s/coll-of ::m.accounts/id))
(s/def ::delete!-request (s/keys :req [::m.accounts/id]))
(s/def ::delete!-response (s/keys :opt [::mu/errors ::mu/status ::deleted-records]))
