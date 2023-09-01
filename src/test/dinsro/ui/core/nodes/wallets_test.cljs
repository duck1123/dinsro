(ns dinsro.ui.core.nodes.wallets-test
  (:require
   [dinsro.mocks.ui.core.nodes.wallets :as mo.u.c.n.wallets]
   [dinsro.test-helpers :as th]
   [dinsro.ui.core.nodes.wallets :as u.c.n.wallets]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../../main/dinsro/ui/core/nodes/wallets.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreNodeWalletsReport
  {::wsm/card-width 6 ::wsm/card-height 12}
  (th/fulcro-card u.c.n.wallets/Report mo.u.c.n.wallets/Report-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreNodeWalletsSubPage
  {::wsm/card-width 6 ::wsm/card-height 12}
  (th/fulcro-card u.c.n.wallets/SubPage mo.u.c.n.wallets/SubPage-data {}))
