(ns dinsro.ui.admin.ln.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.peers :as j.ln.peers]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.ln.peers :as o.ln.peers]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.forms.admin.ln.peers :as u.f.a.ln.peers]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/ln/peers.cljc]]
;; [[../../../model/ln/peers.cljc]]

(def index-page-id :admin-ln-peers)
(def model-key o.ln.peers/id)
(def parent-router-id :admin-ln)
(def required-role :admin)
(def show-page-id :admin-ln-peers-id)

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.a.ln.peers/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.peers/node
                        m.ln.peers/remote-node
                        m.ln.peers/inbound?]
   ro/controls         {::new new-button}
   ro/field-formatters {o.ln.peers/node        #(u.links/ui-node-link %2)
                        o.ln.peers/remote-node #(u.links/ui-remote-node-link %2)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk           m.ln.peers/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.peers/index
   ro/title            "Lightning Peers"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query            (fn []
                       [o.navlinks/id
                        {:ui/report (comp/get-query Report)}])
   :route-segment     ["peers"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Peers"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Peers"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
