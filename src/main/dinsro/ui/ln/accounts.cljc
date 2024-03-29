(ns dinsro.ui.ln.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.accounts :as j.ln.accounts]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/ln/accounts.cljc]]
;; [[../../model/ln/accounts.cljc]]

(def index-page-id :ln-accounts)
(def model-key ::m.ln.accounts/id)
(def parent-router-id :ln)
(def required-role :user)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.accounts/node #(u.links/ui-node-link %2)}
   ro/columns           [m.ln.accounts/id
                         m.ln.accounts/node]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.accounts/index
   ro/title             "Lightning Accounts Report"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["accounts"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Index LN Accounts"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
