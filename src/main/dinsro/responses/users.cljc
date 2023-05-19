(ns dinsro.responses.users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.mutations :as mu]))

(defsc DeleteResponse
  [_ _]
  {:initial-state {::mu/status          :initial
                   ::mu/errors          {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::deleted-records]})
