(ns dinsro.responses.nostr.badge-definitions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.mutations :as mu]))

;; [[../../actions/nostr/badge_definitions.clj]]
;; [[../../mutations/nostr/badge_definitions.cljc]]

(def model-key ::m.n.badge-definitions/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
