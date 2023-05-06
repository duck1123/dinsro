(ns dinsro.responses.nostr.event-tags
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.mutations :as mu]))

;; [[../../mutations/nostr/event_tags.cljc]]
;; [[../../ui/nostr/event_tags.cljs]]

(defsc FetchResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
