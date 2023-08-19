(ns dinsro.ui.admin.ln.nodes.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.addresses :as j.c.addresses]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../ui/admin/ln/nodes.cljc]]

(def index-page-key :admin-ln-nodes-show-addresses)
(def model-key ::m.c.addresses/id)
(def parent-model-key ::m.ln.nodes/id)
(def parent-router :admin-ln-nodes)
(def parent-show-key :admin-ln-nodes-show)
(def router-key :dinsro.ui.admin.ln.nodes/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.addresses/id]
   ro/control-layout   {:action-buttons [::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {parent-model-key {:type :uuid :label "Nodes"}
                        ::refresh        u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.c.addresses/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.addresses/admin-index
   ro/title            "Addresses"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key props
                         ::m.navlinks/id  index-page-key})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (get props parent-model-key)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin nodes show addresses report"))
    (u.debug/load-error props "admin nodes show addresses")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Addresses"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-show-key
   ::m.navlinks/router        parent-router
   ::m.navlinks/required-role :admin})
