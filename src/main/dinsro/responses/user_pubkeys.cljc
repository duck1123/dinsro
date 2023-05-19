(ns dinsro.responses.user-pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.mutations :as mu]))

(defsc DeleteResponse
  [_ _]
  {:initial-state {::mu/status          :initial
                   ::mu/errors          {}
                   ::deleted-records    []}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::deleted-records]})
