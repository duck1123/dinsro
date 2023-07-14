(ns dinsro.ui.accounts.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.transactions :as u.transactions]
   [lambdaisland.glogc :as log]))

;; [[../joins/transactions.cljc]]
;; [[../model/accounts.cljc]]
;; [[../model/transactions.cljc]]

(def index-page-key :accounts-show-transactions)
(def model-key ::m.transactions/id)
(def parent-model-key ::m.accounts/id)
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
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.transactions/index
   ro/title             "Transactions"}
  (let [{:ui/keys [current-rows]} props]
    (dom/div :.ui.items
      (map u.transactions/ui-body-item  current-rows))))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["transactions"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/debug :SubPage/starting {:props props})
  (if report
    (ui-report report)
    (ui-segment {} "Failed to load page")))
