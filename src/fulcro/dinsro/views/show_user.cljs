(ns dinsro.views.show-user
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.show-user :as u.show-user]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [dinsro.ui.user-categories :as u.user-categories]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [taoensso.timbre :as timbre]))

(defsc ShowUserPage
  [_this {:keys [user user-accounts user-categories user-transactions]}]
  {:initial-state {:user {}
                   :user-accounts {}}
   :query [{:user (comp/get-query u.show-user/ShowUser)}
           {:user-accounts (comp/get-query u.user-accounts/UserAccounts)}
           {:user-categories (comp/get-query u.user-categories/UserCategories)}
           {:user-transactions (comp/get-query u.user-transactions/UserTransactions)}]}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/div
      :.box
      (u.show-user/ui-show-user user)
      (u.user-accounts/ui-user-accounts user-accounts)
      (u.user-categories/ui-user-categories user-categories)
      (u.user-transactions/ui-user-transactions user-transactions)
      (dom/div "Show User Page"))))))
