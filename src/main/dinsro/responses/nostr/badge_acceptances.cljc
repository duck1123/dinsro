(ns dinsro.responses.nostr.badge-acceptances
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.mutations :as mu]))

;; [[../../mutations/nostr/badge-acceptances.cljc]]

(def model-key ::m.n.badge-acceptances/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
