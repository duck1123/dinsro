(ns dinsro.responses.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations :as mu]))

;; [../mutations/rate_sources.cljc]
;; [../processors/rate_sources.clj]

(defsc DeletedRateSource
  [_ _]
  {:initial-state {::m.rate-sources/id nil}
   :ident         ::m.rate-sources/id
   :query         [::m.rate-sources/id]})

(defsc DeleteResponse
  [_ _]
  {:initial-state {::mu/status       :initial
                   ::mu/errors       {}
                   ::deleted-records []}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   {::deleted-records (comp/get-query DeletedRateSource)}]})
