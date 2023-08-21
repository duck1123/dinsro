(ns dinsro.responses.nostr.event-tags
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.mutations :as mu]))

;; [[../../mutations/nostr/event_tags.cljc]]
;; [[../../processors/nostr/event_tags.clj]]
;; [[../../ui/nostr/event_tags.cljs]]

(def model-key ::m.n.event-tags/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(defsc FetchResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
