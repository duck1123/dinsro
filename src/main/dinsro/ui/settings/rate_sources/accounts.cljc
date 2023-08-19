(ns dinsro.ui.settings.rate-sources.accounts
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
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def index-page-key :settings-rate-sources-show-accounts)
(def parent-model-key ::m.rate-sources/id)
(def router-key :dinsro.ui.settings.rate-sources/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.accounts/name     #(u.links/ui-account-link %3)
                         ::m.accounts/currency #(u.links/ui-currency-link %2)}
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
   ro/source-attribute  ::j.accounts/index
   ro/title             "Accounts"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]}
  (ui-report report))

(m.navlinks/defroute   :settings-rate-sources-show-accounts
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Accounts"
   ::m.navlinks/input-key     ::m.rate-sources/id
   ::m.navlinks/model-key     ::m.accounts/id
   ::m.navlinks/parent-key    :settings-rate-sources-show
   ::m.navlinks/router        :settings-rate-sources
   ::m.navlinks/required-role :user})
