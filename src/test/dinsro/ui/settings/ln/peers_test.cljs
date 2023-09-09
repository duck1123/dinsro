(ns dinsro.ui.settings.ln.peers-test
  (:require
   [dinsro.mocks.ui.settings.ln.peers :as mo.u.s.ln.peers]
   [dinsro.test-helpers :as th]
   [dinsro.ui.settings.ln.peers :as u.ln.peers]
   [nubank.workspaces.core :as ws]))

;; [[../../../../../main/dinsro/ui/settings/ln/peers.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard LNPeersReport
  (th/fulcro-card u.ln.peers/Report mo.u.s.ln.peers/Report-data {}))

