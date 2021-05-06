(ns dinsro.views.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.admin-index-accounts :as u.admin-index-accounts]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [dinsro.ui.admin-index-currencies :as u.admin-index-currencies]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]
   [dinsro.ui.admin-index-transactions :as u.admin-index-transactions]
   [dinsro.ui.admin-index-users :as u.admin-index-users]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [dinsro.ui.index-users :as u.index-users]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as timbre]))

(defsc AdminPage
  [_this {::keys [accounts categories currencies rate-sources transactions users]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :all-accounts
               u.admin-index-accounts/AdminIndexAccountLine
               {:target [:component/id
                         ::u.admin-index-accounts/AdminIndexAccounts
                         ::u.admin-index-accounts/accounts]})

     (df/load! this :all-categories
               u.admin-index-categories/AdminIndexCategoryLine
               {:target [:component/id
                         ::u.admin-index-categories/AdminIndexCategories
                         ::u.admin-index-categories/categories]})

     (df/load! this :all-currencies
               u.admin-index-currencies/AdminIndexCurrencyLine
               {:target [:component/id
                         ::u.admin-index-currencies/AdminIndexCurrencies
                         ::u.admin-index-currencies/currencies]})

     (df/load! this :all-rate-sources
               u.admin-index-rate-sources/AdminIndexRateSourceLine
               {:target [:component/id
                         ::u.admin-index-rate-sources/AdminIndexRateSources
                         ::u.admin-index-rate-sources/rate-sources]})

     (df/load! this :all-transactions
               u.index-transactions/IndexTransactionLine
               {:target [:component/id
                         ::u.admin-index-transactions/AdminIndexTransactions
                         ::u.admin-index-transactions/transactions
                         ::u.index-transactions/transactions]})

     (df/load! this :all-users
               u.index-users/IndexUserLine
               {:target [:component/id
                         ::u.admin-index-users/AdminIndexUsers
                         ::u.admin-index-users/users
                         ::u.index-users/items]}))
   :ident (fn [_] [:page/id ::page])
   :initial-state {::accounts     {}
                   ::categories   {}
                   ::currencies   {}
                   ::rate-sources {}
                   ::transactions {}
                   ::users        {}}
   :query [{::accounts     (comp/get-query u.admin-index-accounts/AdminIndexAccounts)}
           {::categories   (comp/get-query u.admin-index-categories/AdminIndexCategories)}
           {::currencies   (comp/get-query u.admin-index-currencies/AdminIndexCurrencies)}
           :page/id
           {::rate-sources (comp/get-query u.admin-index-rate-sources/AdminIndexRateSources)}
           {::transactions (comp/get-query u.admin-index-transactions/AdminIndexTransactions)}
           {::users        (comp/get-query u.admin-index-users/AdminIndexUsers)}]
   :route-segment ["admin"]}
  (bulma/page
   (dom/h1 :.title.is-1 "Admin")
   (dom/hr)
   (u.admin-index-accounts/ui-section accounts)
   (u.admin-index-transactions/ui-section transactions)
   (u.admin-index-categories/ui-section categories)
   (u.admin-index-currencies/ui-section currencies)
   (u.admin-index-rate-sources/ui-section rate-sources)
   (u.admin-index-users/ui-section users)))
