(ns dinsro.ui.admin.ln.channels
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.channels :as j.ln.channels]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.ln.channels :as o.ln.channels]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/ln/channels.cljc]]
;; [[../../../model/ln/channels.cljc]]

(def index-page-id :admin-ln-channels)
(def model-key ::m.ln.channels/id)
(def parent-router-id :admin-ln)
(def required-role :admin)
(def show-page-id :admin-ln-channels-show)

(report/defsc-report Report
  [this _props]
  {ro/columns          [m.ln.channels/id
                        m.ln.channels/channel-point
                        m.ln.channels/node]
   ro/field-formatters {o.ln.channels/node #(u.links/ui-node-link %2)
                        o.ln.channels/id   #(u.links/ui-channel-link %3)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk           m.ln.channels/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.channels/index
   ro/title            "Channels"}
  (dom/div {}
    (report/render-layout this)))

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.ln.channels/id
   :initial-state {::m.ln.channels/id nil}
   :query         [::m.ln.channels/id]
   :route-segment ["channels" :id]}
  (dom/div {}))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["channels"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Channels"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Channel"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
