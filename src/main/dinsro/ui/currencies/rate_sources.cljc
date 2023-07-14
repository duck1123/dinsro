(ns dinsro.ui.currencies.rate-sources
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

;; [[../../joins/rate_sources.cljc]]
;; [[../../model/rate_sources.cljc]]

(def ident-key ::m.currencies/id)
(def index-page-key :currencies-show-rate-sources)
(def model-key ::m.rate-sources/id)
(def parent-model-key ::m.currencies/id)
(def router-key :dinsro.ui.currencies/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.rate-sources/name #(u.links/ui-rate-source-link %3)}
   ro/columns           [m.rate-sources/name]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.currencies/id {:type :uuid :label "id"}
                         ::refresh         u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.rate-sources/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.rate-sources/index
   ro/title             "Rate Sources"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:parent-router     router-key
   :parent-ident      ident-key
   :componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["rate-sources"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/debug :SubPage/starting {:props props})
  (if report
    (ui-report report)
    (u.debug/load-error props "currencies rate sources page")))

(m.navlinks/defroute   :currencies-show-rate-sources
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Rates Sources"
   ::m.navlinks/input-key     ::m.currencies/id
   ::m.navlinks/model-key     ::m.rate-sources/id
   ::m.navlinks/parent-key    :currencies-show
   ::m.navlinks/router        :currencies
   ::m.navlinks/required-role :user})
