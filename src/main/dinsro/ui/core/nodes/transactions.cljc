(ns dinsro.ui.core.nodes.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.core.nodes.transactions :as u.r.c.n.transactions]))

;; project://src/main/dinsro/joins/core/transactions.cljc
;; project://src/main/dinsro/model/core/transactions.cljc
;; [[../../../../../test/dinsro/ui/core/nodes/transactions_test.cljs]]

(def index-page-id :core-nodes-show-transactions)
(def model-key ::m.c.transactions/id)
(def parent-model-key ::m.c.nodes/id)
(def parent-router-id :core)
(def required-role :user)
(def router-key :dinsro.ui.core.nodes/Router)

(defsc SubPage
  [_this props]
  {:componentDidMount #(report/start-report! % u.r.c.n.transactions/Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {::m.navlinks/id index-page-id
                         parent-model-key (parent-model-key props)
                         :ui/report      {}})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query u.r.c.n.transactions/Report)}])
   :route-segment     ["transactions"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props u.r.c.n.transactions/ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/label         "Transactions"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
