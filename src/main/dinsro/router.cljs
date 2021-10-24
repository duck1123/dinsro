(ns dinsro.router
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.index-accounts :as u.index-accounts]
   [dinsro.ui.index-categories :as u.index-categories]
   [dinsro.ui.index-currencies :as u.index-currencies]
   [dinsro.ui.index-rates :as u.index-rates]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [dinsro.ui.index-users :as u.index-users]
   [dinsro.ui.login :as u.login]
   [dinsro.ui.registration :as u.registration]
   [dinsro.ui.show-account :as u.show-account]
   [dinsro.ui.show-category :as u.show-category]
   [dinsro.ui.show-currency :as u.show-currency]
   [dinsro.ui.show-rate-source :as u.show-rate-source]
   [dinsro.ui.show-user :as u.show-user]
   [taoensso.timbre :as log]))

(defrouter RootRouter
  [_this {:keys [current-state]}]
  {:router-targets [u.admin/AdminPage
                    u.home/HomePage
                    u.index-accounts/IndexAccountsPage
                    u.index-categories/IndexCategoriesPage
                    u.index-currencies/IndexCurrenciesPage
                    u.index-rates/IndexRatesPage
                    u.index-rate-sources/IndexRateSourcesPage
                    u.index-transactions/IndexTransactionsPage
                    u.index-users/IndexUsersPage
                    u.login/LoginPage
                    u.registration/RegistrationPage
                    u.show-account/ShowAccountPage
                    u.show-category/ShowCategoryPage
                    u.show-currency/ShowCurrencyPage
                    u.show-rate-source/ShowRateSourcePage
                    u.show-user/ShowUserPage]}
  (case current-state
    :pending (dom/div "Loading...")
    :failed  (dom/div "Failed!")
    ;; default will be used when the current state isn't yet set
    (dom/div "No route selected.")))

(def ui-root-router (comp/factory RootRouter))
