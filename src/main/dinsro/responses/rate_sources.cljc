(ns dinsro.responses.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations :as mu]))

;; [../mutations/rate_sources.cljc]
;; [../processors/rate_sources.clj]

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.rate-sources/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
