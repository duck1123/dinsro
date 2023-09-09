(ns dinsro.ui.forms.admin.currencies-test
  (:require
   [dinsro.mocks.ui.forms.admin.currencies :as mo.u.f.a.currencies]
   [dinsro.test-helpers :as th]
   [dinsro.ui.forms.admin.currencies :as u.f.a.currencies]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AdminCurrenciesForm
  {::wsm/card-width  3
   ::wsm/card-height 10}
  (th/fulcro-card u.f.a.currencies/NewForm mo.u.f.a.currencies/get-state {}))
