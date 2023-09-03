(ns dinsro.responses.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.mutations :as mu]
   [dinsro.options.transactions :as o.transactions]))

(def model-key o.transactions/id)

(def deleted-records
  "A list of records that have been deleted"
  ::deleted-records)

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
