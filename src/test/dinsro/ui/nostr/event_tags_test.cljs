(ns dinsro.ui.nostr.event-tags-test
  (:require
   [dinsro.mocks.ui.nostr.event-tags :as mo.u.n.event-tags]
   [dinsro.test-helpers :as th]
   [dinsro.ui.nostr.event-tags :as u.n.event-tags]
   [nubank.workspaces.core :as ws]))

;; [[../../../../main/dinsro/ui/nostr/event_tags.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TagDisplay
  (th/fulcro-card u.n.event-tags/TagDisplay mo.u.n.event-tags/make-tag  {}))
