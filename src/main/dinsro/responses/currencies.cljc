(ns dinsro.responses.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mu]))

;; [../actions/currencies.clj]
;; [../mutations/currencies.cljc]
;; [../processors/currencies.clj]

(s/def ::deleted-records (s/coll-of ::m.currencies/id))
(s/def ::delete!-request (s/keys :req [::m.currencies/id]))
(s/def ::delete!-response (s/keys :opt [::mu/errors ::mu/status ::deleted-records]))

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.currencies/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
