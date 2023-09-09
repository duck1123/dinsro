(ns dinsro.ui.core.wallets-test
  (:require
   [dinsro.client :as client]
   [dinsro.mocks.ui.core.wallets :as mo.u.c.wallets]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/ui/core/wallets.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard WalletReport
  {::wsm/card-width 6 ::wsm/card-height 12}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.wallets/Report
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state mo.u.c.wallets/Report-data}))
