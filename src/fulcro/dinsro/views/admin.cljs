(ns dinsro.views.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.admin-index-accounts :as u.admin-index-accounts]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [dinsro.ui.admin-index-currencies :as u.admin-index-currencies]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]
   [dinsro.ui.admin-index-transactions :as u.admin-index-transactions]
   [dinsro.ui.admin-index-users :as u.admin-index-users]
   [taoensso.timbre :as timbre]))

(defsc AdminPage
  [_this {:keys [accounts transactions categories currencies rate-sources users]}]
  {:query [:page/id
           {:accounts (comp/get-query u.admin-index-accounts/AdminIndexAccounts)}
           {:transactions (comp/get-query u.admin-index-transactions/AdminIndexTransactions)}
           {:categories (comp/get-query u.admin-index-categories/AdminIndexCategories)}
           {:currencies (comp/get-query u.admin-index-currencies/AdminIndexCurrencies)}
           {:rate-sources (comp/get-query u.admin-index-rate-sources/AdminIndexRateSources)}
           {:users (comp/get-query u.admin-index-users/AdminIndexUsers)}]
   :ident (fn [_] [:page/id ::page])
   :route-segment ["admin"]}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/div
      :.box
      (dom/h1
       :.title
       "Admin"))
     (u.admin-index-accounts/ui-section accounts)
     (u.admin-index-transactions/ui-section transactions)
     (u.admin-index-categories/ui-section categories)
     (u.admin-index-currencies/ui-section currencies)
     (u.admin-index-rate-sources/ui-section rate-sources)
     (u.admin-index-users/ui-section users)))

   "admin page"))
