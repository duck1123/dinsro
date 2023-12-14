(ns dinsro.ui.admin.accounts.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.admin.accounts.transactions :as u.r.a.a.transactions]))

;; [[../joins/transactions.cljc]]
;; [[../model/accounts.cljc]]
;; [[../model/transactions.cljc]]

(def index-page-id :admin-accounts-show-transactions)
(def parent-model-key o.accounts/id)
(def router-key :dinsro.ui.admin.accounts/Router)

;; actually a sub-section?
(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key u.r.a.a.transactions/Report)
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id    index-page-id
                         :ui/report       (comp/get-initial-state u.r.a.a.transactions/Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         o.navlinks/id
                         {:ui/report (comp/get-query u.r.a.a.transactions/Report)}])
   :route-segment     ["transactions"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props u.r.a.a.transactions/ui-report parent-model-key :ui/report))
