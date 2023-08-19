(ns dinsro.ui.admin.ln.nodes.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.accounts :as j.ln.accounts]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../ui/admin/ln/nodes.cljc]]

(def index-page-key :admin-ln-nodes-show-accounts)
(def model-key ::m.ln.accounts/id)
(def parent-model-key ::m.ln.nodes/id)
(def parent-router :admin-ln-nodes)
(def parent-show-key :admin-ln-nodes-show)
(def router-key :dinsro.ui.admin.ln.nodes/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.accounts/id
                        m.ln.accounts/node]
   ro/field-formatters {::m.ln.accounts/node #(u.links/ui-node-link %2)}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/route            "accounts"
   ro/row-pk           m.ln.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.accounts/admin-index
   ro/title            "Accounts"})

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
      (u.debug/load-error props "admin nodes show accounts report"))
    (u.debug/load-error props "admin nodes show accounts")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Accounts"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-show-key
   ::m.navlinks/router        parent-router
   ::m.navlinks/required-role :admin})
