(ns dinsro.responses.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations :as mu]))

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.transactions/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
