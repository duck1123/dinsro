(ns dinsro.ui.core.nodes-test
  (:require
   [dinsro.client :as client]
   [dinsro.mocks.ui.core.nodes :as mo.u.c.nodes]
   [dinsro.mocks.ui.core.nodes.peers :as mo.u.c.n.peers]
   [dinsro.test-helpers :as th]
   [dinsro.ui.core.nodes :as u.c.nodes]
   [dinsro.ui.core.nodes.peers :as u.c.node-peers]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/mocks/ui/core/nodes.cljc]]
;; [[../../../../main/dinsro/ui/core/nodes.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreNodesActionsMenu
  {::wsm/card-width 2 ::wsm/card-height 9}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.c.nodes/ActionsMenu
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state mo.u.c.nodes/actions-menu-data}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreNodesSubPage
  {::wsm/card-width 7 ::wsm/card-height 20}
  (th/fulcro-card u.c.node-peers/SubPage mo.u.c.n.peers/SubPage-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreNodesShow
  {::wsm/card-width 7 ::wsm/card-height 14}
  (th/fulcro-card u.c.nodes/Show mo.u.c.nodes/Show-data {}))
