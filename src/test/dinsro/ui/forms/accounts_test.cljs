(ns dinsro.ui.forms.accounts-test
  (:require
   [dinsro.mocks.ui.forms.accounts :as mo.u.f.accounts]
   [dinsro.test-helpers :as th]
   [dinsro.ui.forms.accounts :as u.f.accounts]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/mocks/ui/forms/accounts.cljc]]
;; [[../../../../main/dinsro/ui/forms/accounts.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AccountsCurrencyListItem
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.accounts/CurrencyListItem mo.u.f.accounts/CurrencyListItem-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AccountsNewForm
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.accounts/NewForm mo.u.f.accounts/NewForm-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AccountsInlineForm-form
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.accounts/InlineForm-form mo.u.f.accounts/InlineForm-form-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AccountsInlineForm-component
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.accounts/InlineForm-component mo.u.f.accounts/InlineForm-component-data {}))
