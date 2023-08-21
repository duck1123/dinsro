(ns dinsro.responses.nostr.badge-awards
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.mutations :as mu]))

;; [[../../actions/nostr/badge_awards.clj]]
;; [[../../mutations/nostr/badge_awards.cljc]]

(def model-key ::m.n.badge-awards/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
