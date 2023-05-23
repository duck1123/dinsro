(ns dinsro.responses.users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.users :as m.users]
   [dinsro.mutations :as mu]))

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.users/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
