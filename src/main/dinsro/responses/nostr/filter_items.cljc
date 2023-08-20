(ns dinsro.responses.nostr.filter-items
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.mutations :as mu]))

;; [[../../mutations/nostr/filter-items.cljc]]

(def model-key ::m.n.filter-items/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
