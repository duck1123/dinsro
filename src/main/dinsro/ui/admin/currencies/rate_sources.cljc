(ns dinsro.ui.admin.currencies.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/rate_sources.cljc]]
;; [[../../../model/rate_sources.cljc]]

(def index-page-key :admin-currencies-show-rate-sources)
(def model-key ::m.rate-sources/id)
(def parent-model-key ::m.currencies/id)
(def router-key :dinsro.ui.admin.currencies/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.rate-sources/name #(u.links/ui-admin-rate-source-link %3)}
   ro/columns           [m.rate-sources/name]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh         u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.rate-sources/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.rate-sources/admin-index
   ro/title             "Rate Sources"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["rate-sources"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/debug :SubPage/starting {:props props})
  (if (parent-model-key props)
    (if report
      (ui-report report)
      (u.debug/load-error props "currencies rate sources report"))
    (u.debug/load-error props "currencies rate sources page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Rates Sources"
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :currencies-show
   ::m.navlinks/router        :currencies
   ::m.navlinks/required-role :user})
