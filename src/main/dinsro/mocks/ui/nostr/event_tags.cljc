(ns dinsro.mocks.ui.nostr.event-tags
  (:require
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.specs :as ds]))

;; [[../../../ui/nostr/event_tags.cljc]]

(defn make-tag
  []
  {::m.n.event-tags/id        (ds/gen-key ::m.n.event-tags/id)
   ::m.n.event-tags/pubkey    nil
   ::m.n.event-tags/event     nil
   ::m.n.event-tags/index     (ds/gen-key ::m.n.event-tags/index)
   ::m.n.event-tags/raw-value (ds/gen-key ::m.n.event-tags/raw-value)
   ::m.n.event-tags/type      "e"})
