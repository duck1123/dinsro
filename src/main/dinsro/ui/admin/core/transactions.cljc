(ns dinsro.ui.admin.core.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.admin.core.transactions.inputs :as u.a.c.t.inputs]
   [dinsro.ui.admin.core.transactions.outputs :as u.a.c.t.outputs]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]))

;; [[../../../joins/core/transactions.cljc]]
;; [[../../../model/core/transactions.cljc]]

(def index-page-id :admin-core-transactions)
(def model-key ::m.c.transactions/id)
(def parent-router-id :admin-core)
(def required-role :admin)
(def show-page-id :admin-core-transactions-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.transactions/delete!))

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.transactions/fetch!))

(defsc Show
  "Show a core tx"
  [this {::m.c.transactions/keys [id tx-id hash fetched? block size]
         :ui/keys                [admin-inputs admin-outputs]
         :as                     props}]
  {:ident         ::m.c.transactions/id
   :initial-state {::m.c.transactions/block    {}
                   ::m.c.transactions/fetched? false
                   ::m.c.transactions/hash     ""
                   ::m.c.transactions/id       nil
                   ::m.c.transactions/size     0
                   ::m.c.transactions/tx-id    nil
                   :ui/admin-inputs            {}
                   :ui/admin-outputs           {}}
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                     :ui/admin-inputs   [u.a.c.t.inputs/SubSection {}]
                     :ui/admin-outputs   [u.a.c.t.outputs/SubSection {}]})
   :query         [[df/marker-table '_]
                   {::m.c.transactions/block (comp/get-query u.links/BlockHeightLinkForm)}
                   ::m.c.transactions/fetched?
                   ::m.c.transactions/hash
                   ::m.c.transactions/id
                   ::m.c.transactions/size
                   ::m.c.transactions/tx-id
                   {:ui/admin-inputs (comp/get-query u.a.c.t.inputs/SubSection)}
                   {:ui/admin-outputs (comp/get-query u.a.c.t.outputs/SubSection)}]}
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} "Transaction")
        (dom/dl {}
          (dom/dt {} "TX id")
          (dom/dd {} (str tx-id))
          (dom/dt {} "Hash: ")
          (dom/dd {} (str hash))
          (dom/dt {} "Block: ")
          (dom/dd {} (u.links/ui-block-height-link block))
          (dom/dt {} "Fetched")
          (dom/dd {} (dom/a {:onClick #(comp/transact! this [`(mu.c.transactions/fetch! {~model-key ~id})])
                             :href    "#"}
                       (str fetched?)))
          (dom/dt {} "Size")
          (dom/dd {} (str size))))
      (dom/div {}
        (if admin-inputs
          (ui-segment {}
            (u.a.c.t.inputs/ui-subsection admin-inputs))
          (u.debug/load-error props "Admin show core transaction inputs"))
        (if admin-outputs
          (ui-segment {}
            (u.a.c.t.outputs/ui-subsection admin-outputs))
          (u.debug/load-error props "Admin show core transaction outputs"))))
    (u.debug/load-error props "Admin show core transaction record")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.transactions/block #(u.links/ui-admin-block-height-link %2)
                         ::m.c.transactions/tx-id #(u.links/ui-admin-core-tx-link %3)}
   ro/columns           [m.c.transactions/tx-id
                         m.c.transactions/fetched?
                         m.c.transactions/block]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [fetch-action delete-action]
   ro/row-pk            m.c.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.transactions/index
   ro/title             "Transactions"})

(def ui-report (comp/factory Report))

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
   :route-segment     ["transactions"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["transaction" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Transactions"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Transaction"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
