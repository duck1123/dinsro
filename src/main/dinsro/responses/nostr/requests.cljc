(ns dinsro.responses.nostr.requests
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations :as mu]))

;; [[../../mutations/nostr/requests.cljc]]

(def model-key ::m.n.requests/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
