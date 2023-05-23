(ns dinsro.responses.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.debits :as m.debits]
   [dinsro.mutations :as mu]))

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.debits/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
