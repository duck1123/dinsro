(ns dinsro.processors.nostr.event-tags
  (:require
   [dinsro.actions.nostr.event-tags :as a.n.event-tags]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/event_tags.clj]]
;; [[../../mutations/nostr/event_tags.cljc]]

(defn fetch!
  [_env props]
  (log/info :fetch!/starting {:props props})
  (a.n.event-tags/fetch! props)
  {::mu/status :ok})
