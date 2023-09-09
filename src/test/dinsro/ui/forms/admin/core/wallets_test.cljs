(ns dinsro.ui.forms.admin.core.wallets-test
  (:require
   [dinsro.mocks.ui.forms.admin.core.wallets :as mo.u.f.a.c.wallets]
   [dinsro.test-helpers :as th]
   [dinsro.ui.forms.admin.core.wallets :as u.f.a.c.wallets]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../../../main/dinsro/mocks/ui/forms/admin/core/wallets.cljc]]
;; [[../../../../../../main/dinsro/ui/forms/admin/core/wallets.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AdminWalletsForm
  {::wsm/card-width  3
   ::wsm/card-height 10}
  (th/fulcro-card u.f.a.c.wallets/NewForm mo.u.f.a.c.wallets/NewForm-state {}))
