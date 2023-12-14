(ns dinsro.ui.nostr.events-test
  (:require
   [dinsro.mocks.ui.nostr.events :as mo.u.n.events]
   [dinsro.test-helpers :as th]
   [dinsro.ui.nostr.events :as u.n.events]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/mocks/ui/nostr/events.cljc]]
;; [[../../../../main/dinsro/ui/nostr/events.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard EventAuthorImage
  {::wsm/card-height 14 ::wsm/card-width 4}
  (th/fulcro-card u.n.events/EventAuthorImage mo.u.n.events/EventAuthorImage-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard EventAuthor
  {::wsm/card-height 5 ::wsm/card-width 3}
  (th/fulcro-card u.n.events/EventAuthor mo.u.n.events/EventAuthor-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard EventBox
  {::wsm/card-width 9 ::wsm/card-height 10}
  (th/fulcro-card u.n.events/EventBox mo.u.n.events/EventBox-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard EventReport
  {::wsm/card-width 10 ::wsm/card-height 16}
  (th/fulcro-card u.n.events/Report mo.u.n.events/EventReport-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard ShowEvent
  {::wsm/card-width 10 ::wsm/card-height 16}
  (th/fulcro-card u.n.events/Show mo.u.n.events/ShowEvent-data {}))
