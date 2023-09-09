(ns dinsro.ui.forms.admin.users-test
  (:require
   [dinsro.mocks.ui.forms.admin.users :as mo.u.f.a.users]
   [dinsro.test-helpers :as th]
   [dinsro.ui.forms.admin.users :as u.f.a.users]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../../main/dinsro/mocks/ui/forms/admin/users.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AdminUserForm
  {::wsm/card-width 9 ::wsm/card-height 20}
  (th/fulcro-card u.f.a.users/UserForm mo.u.f.a.users/get-state {}))
