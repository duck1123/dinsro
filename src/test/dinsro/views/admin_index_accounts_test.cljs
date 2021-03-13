(ns dinsro.views.admin-index-accounts-test
  (:require
   [dinsro.views.admin-index-accounts :as v.admin-index-accounts]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [taoensso.timbre :as timbre]))

(ws/defcard AdminIndexAccountsPage
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.admin-index-accounts/AdminIndexAccountsPage
    ::ct.fulcro3/initial-state (fn [] {})
    ::ct.fulcro3/wrap-root? false}))
