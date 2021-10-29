(ns dinsro.router
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.currencies :as u.currrencies]
   [dinsro.ui.rates :as u.rates]
   [dinsro.ui.login :as u.login]
   [dinsro.ui.rate-sources :as u.rate-sources]
   [dinsro.ui.registration :as u.registration]
   [dinsro.ui.transactions :as u.transactions]
   [dinsro.ui.users :as u.users]
   [taoensso.timbre :as log]))

(defrouter RootRouter
  [_this {:keys [current-state]}]
  {:router-targets [u.accounts/AccountForm
                    u.accounts/AccountsReport
                    u.admin/AdminPage
                    u.categories/CategoryForm
                    u.categories/CategoriesReport
                    u.currrencies/CurrencyForm
                    u.currrencies/CurrenciesReport
                    u.home/HomePage
                    u.login/LoginPage
                    u.registration/RegistrationPage
                    u.rate-sources/RateSourceForm
                    u.rate-sources/RateSourcesReport
                    u.rates/RateForm
                    u.rates/RatesReport
                    u.transactions/TransactionForm
                    u.transactions/TransactionsReport
                    u.users/UserForm
                    u.users/UsersReport]}
  (case current-state
    :pending (dom/div "Loading...")
    :failed  (dom/div "Failed!")
    ;; default will be used when the current state isn't yet set
    (dom/div "No route selected.")))

(def ui-root-router (comp/factory RootRouter))
