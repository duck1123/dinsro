(ns dinsro.ui.forms.admin.users.categories-test
  (:require
   [dinsro.client :as client]
   [dinsro.mocks.ui.forms.admin.users.categories :refer [make-body-item]]
   [dinsro.ui.admin.users.categories :as u.a.u.categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AdminUserCategoriesNewForm
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.a.u.categories/NewForm
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state (fn [] (make-body-item))}))
