(ns dinsro.ui.accounts-test
  (:require
   [dinsro.mocks.ui.accounts :as mo.u.accounts]
   [dinsro.test-helpers :as th]
   [dinsro.ui.accounts :as u.accounts]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../main/dinsro/mocks/ui/accounts.cljc]]
;; [[../../../main/dinsro/ui/accounts.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard ShowAccounts
  {::wsm/card-height 11 ::wsm/card-width 5}
  (th/fulcro-card u.accounts/Show mo.u.accounts/Show-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard IndexAccounts
  {::wsm/card-height 11 ::wsm/card-width 5}
  (th/fulcro-card u.accounts/IndexPage mo.u.accounts/IndexPage-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard ShowAccountsPage
  {::wsm/card-height 11 ::wsm/card-width 5}
  (th/fulcro-card u.accounts/ShowPage mo.u.accounts/ShowPage-data {}))
