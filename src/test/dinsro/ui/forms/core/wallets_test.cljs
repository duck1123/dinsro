(ns dinsro.ui.forms.core.wallets-test
  (:require
   [dinsro.client :as client]
   [dinsro.mocks.ui.core.wallets :as mo.u.c.wallets]
   [dinsro.ui.forms.core.wallets :as u.f.c.wallets]
   [lambdaisland.glogc :as log]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard WalletNewForm
  {::wsm/card-width 6 ::wsm/card-height 12}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.c.wallets/NewForm
    ::ct.fulcro3/app
    {:client-will-mount
     (fn [app]
       (let [response (client/setup-RAD app)]
         (log/info :NewWalletForm/will-mount {:response response})
         response))

     :global-error-action
     (fn [env]
       (log/info :global/error {:env env}))

     :submit-transaction!
     (fn [app tx]
       (log/info :submit-transaction!/creating {:app app :env tx}))}

    ::ct.fulcro3/initial-state mo.u.c.wallets/NewForm-data}))
