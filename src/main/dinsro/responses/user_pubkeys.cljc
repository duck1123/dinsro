(ns dinsro.responses.user-pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.user-pubkeys :as m.user-pubkeys]
   [dinsro.mutations :as mu]))

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records    []
                   ::mu/status          :initial
                   ::mu/errors          {}}
   :query         [{::deleted-records [::m.user-pubkeys/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
