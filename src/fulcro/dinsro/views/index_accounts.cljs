(ns dinsro.views.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [taoensso.timbre :as timbre]))

(defsc IndexAccountsPage
  [_this {:keys [accounts]}]
  {:query [{:accounts (comp/get-query u.user-accounts/UserAccounts)}]
   :initial-state (fn [_] {:accounts []})}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (u.user-accounts/ui-user-accounts accounts)))))

(def ui-page (comp/factory IndexAccountsPage))
