(ns dinsro.ui.forms.settings.ln.peers-test
  (:require
   [dinsro.mocks.ui.settings.ln.peers :as mo.u.s.ln.peers]
   [dinsro.test-helpers :as th]
   [dinsro.ui.forms.settings.ln.peers :as u.f.ln.peers]
   [nubank.workspaces.core :as ws]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard LNPeersNewForm
  (th/fulcro-card u.f.ln.peers/NewForm mo.u.s.ln.peers/NewForm-data {}))
