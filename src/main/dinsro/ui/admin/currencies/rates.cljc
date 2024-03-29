(ns dinsro.ui.admin.currencies.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.rates :as j.rates]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.rates :as m.rates]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.rates :as o.rates]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../joins/rates.cljc]]
;; [[../../model/rates.cljc]]

(def index-page-id :currencies-show-rates)
(def model-key o.rates/id)
(def parent-model-key o.currencies/id)
(def parent-router-id :admin-currencies-show)
(def required-role :admin)
(def router-key :dinsro.ui.currencies/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.rates/rate #(u.links/ui-rate-link %3)}
   ro/columns           [m.rates/rate]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.rates/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.rates/index
   ro/title             "Rates"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id    index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["rates"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/label         "Rates"
   o.navlinks/input-key     parent-model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
