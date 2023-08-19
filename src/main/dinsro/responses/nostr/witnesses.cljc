(ns dinsro.responses.nostr.witnesses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.mutations :as mu]))

;; [[../../mutations/nostr/witnesses.cljc]]

(def model-key ::m.n.witnesses/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
