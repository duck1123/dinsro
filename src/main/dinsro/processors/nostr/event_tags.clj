(ns dinsro.processors.nostr.event-tags
  (:require
   [dinsro.actions.nostr.event-tags :as a.n.event-tags]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/event_tags.clj]]
;; [[../../model/nostr/event_tags.cljc]]
;; [[../../mutations/nostr/event_tags.cljc]]

(defn fetch!
  [_env props]
  (log/info :fetch!/starting {:props props})
  (if-let [id (::m.n.event-tags/id props)]
    (do
      (a.n.event-tags/fetch! id)
      {::mu/status :ok})
    (mu/error-response "No ID")))
