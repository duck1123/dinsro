(ns dinsro.ui.admin.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/rate_sources.cljc]]
;; [[../../model/rate_sources.cljc]]
;; [[../../mutations/rate_sources.cljc]]

(def index-page-key :admin-rate-sources)
(def model-key ::m.rate-sources/id)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.rate-sources/name
                        j.rate-sources/rate-count]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-actions      [(u.buttons/row-action-button "Delete" model-key mu.rate-sources/delete!)]
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.rate-sources/index
   ro/title            "Rate Sources"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["rate-sources"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))
