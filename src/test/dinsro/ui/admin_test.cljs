(ns dinsro.ui.admin-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.ui.admin-index-accounts :as u.admin-index-accounts]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [dinsro.ui.admin-index-currencies :as u.admin-index-currencies]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]
   [dinsro.ui.admin-index-transactions :as u.admin-index-transactions]
   [dinsro.ui.admin-index-users :as u.admin-index-users]
   [dinsro.ui.admin :as u.admin]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard AdminPage
  {::wsm/card-height 29
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin/AdminPage
    ::ct.fulcro3/initial-state
    (fn []
      {::u.admin/accounts     (comp/get-initial-state u.admin-index-accounts/AdminIndexAccounts {})
       ::u.admin/categories   (comp/get-initial-state u.admin-index-categories/AdminIndexCategories {})
       ::u.admin/currencies   (comp/get-initial-state u.admin-index-currencies/AdminIndexCurrencies {})
       ::u.admin/rate-sources (comp/get-initial-state u.admin-index-rate-sources/AdminIndexRateSources {})
       ::u.admin/transactions (comp/get-initial-state u.admin-index-transactions/AdminIndexTransactions {})
       ::u.admin/users        (comp/get-initial-state u.admin-index-users/AdminIndexUsers {})})}))
