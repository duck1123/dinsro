(ns dinsro.ui.accounts.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.transactions :as u.transactions]))

;; [[../joins/transactions.cljc][Transaction Joins]]
;; [[../model/accounts.cljc][Accounts Model]]
;; [[../model/transactions.cljc][Transactions Model]]

(def ident-key ::m.accounts/id)
(def router-key :dinsro.ui.accounts/Router)

(report/defsc-report Report
  [_this props]
  {ro/BodyItem          u.transactions/BodyItem
   ro/column-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/columns           [m.transactions/description
                         j.transactions/debit-count
                         m.transactions/date]
   ro/control-layout    {:inputs         [[::m.accounts/id]]
                         :action-buttons [::refresh]}
   ro/controls          {::m.accounts/id {:type :uuid :label "id"}
                         ::refresh       u.links/refresh-control}
   ro/row-pk            m.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.transactions/index
   ro/title             "Transactions"}
  (let [{:ui/keys [current-rows]} props]
    (dom/div :.ui.items
      (map u.transactions/ui-body-item  current-rows))))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys   [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report      {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["transactions"]}
  ((comp/factory Report) report))

(def ui-sub-page (comp/factory SubPage))
