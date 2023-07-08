(ns dinsro.ui.admin.rate-sources.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def ident-key ::m.rate-sources/id)
(def index-page-key :admin-rate-sources-show-accounts)
(def router-key :dinsro.ui.admin.rate-sources/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.accounts/name     #(u.links/ui-admin-account-link %3)
                         ::m.accounts/currency #(u.links/ui-admin-currency-link %2)}
   ro/columns           [m.accounts/name
                         m.accounts/currency
                         m.accounts/wallet]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.rate-sources/id {:type :uuid :label "id"}
                         ::refresh           u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/admin-index
   ro/title             "Accounts"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.rate-sources/keys [id]
          :ui/keys [report]
          :as props}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       ::m.rate-sources/id nil
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       ::m.rate-sources/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]}
  (if (and report id)
    (ui-report report)
    (u.debug/load-error props "admin rate source accounts page")))
