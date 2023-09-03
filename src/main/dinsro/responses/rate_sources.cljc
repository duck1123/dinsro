(ns dinsro.responses.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.mutations :as mu]
   [dinsro.options.rate-sources :as o.rate-sources]))

;; [../mutations/rate_sources.cljc]
;; [../processors/rate_sources.clj]

(def model-key o.rate-sources/id)

(def deleted-records ::deleted-records)

(defsc DeleteResponse
  [_this _props]
  {:initial-state (fn [_props]
                    {deleted-records []
                     mu/status       :initial
                     mu/errors       {}})
   :query         (fn []
                    [{deleted-records [model-key]}
                     {mu/errors (comp/get-query mu/ErrorData)}
                     mu/status])})
