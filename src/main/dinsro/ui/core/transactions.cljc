(ns dinsro.ui.core.transactions
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
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.options.core.transactions :as o.c.transactions]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.core.transactions.inputs :as u.c.t.inputs]
   [dinsro.ui.core.transactions.outputs :as u.c.t.outputs]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/core/transactions.cljc]]
;; [[../../model/core/transactions.cljc]]
;; [[../../ui/admin/core/transactions.cljs]]

(def index-page-id :core-transactions)
(def model-key o.c.transactions/id)
(def parent-router-id :core)
(def required-role :user)
(def show-page-id :core-transactions-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.transactions/delete!))

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.transactions/fetch!))

(defsc Show
  "Show a core tx"
  [this {::m.c.transactions/keys [id tx-id hash fetched? block size]
         :ui/keys                [inputs outputs]
         :as                     props}]
  {:ident         ::m.c.transactions/id
   :initial-state {::m.c.transactions/id       nil
                   ::m.c.transactions/tx-id    nil
                   ::m.c.transactions/hash     ""
                   ::m.c.transactions/block    {}
                   ::m.c.transactions/size     0
                   :ui/inputs                  {}
                   :ui/outputs                 {}
                   ::m.c.transactions/fetched? false}
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/inputs  [u.c.t.inputs/SubSection {}]
                     :ui/outputs [u.c.t.outputs/SubSection {}]})
   :query         [::m.c.transactions/id
                   ::m.c.transactions/tx-id
                   ::m.c.transactions/hash
                   ::m.c.transactions/size
                   ::m.c.transactions/fetched?
                   {:ui/inputs (comp/get-query u.c.t.inputs/SubSection)}
                   {:ui/outputs (comp/get-query u.c.t.outputs/SubSection)}
                   {::m.c.transactions/block (comp/get-query u.links/BlockHeightLinkForm)}
                   [df/marker-table '_]]}
  (log/info :Show/starting {:props props})
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
          (dom/dd {} (dom/a {:onClick #(comp/transact! this [`(mu.c.transactions/fetch! {::m.c.transactions/id ~id})])
                             :href    "#"}
                       (str fetched?)))
          (dom/dt {} "Size")
          (dom/dd {} (str size))))
      (if id
        (dom/div {}
          (when inputs ((comp/factory u.c.t.inputs/SubSection) inputs))
          (when outputs ((comp/factory u.c.t.outputs/SubSection) outputs)))
        (ui-segment {:color "red" :inverted true}
          "id not set")))
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.c.transactions/block #(u.links/ui-block-height-link %2)
                         o.c.transactions/tx-id #(u.links/ui-core-tx-link %3)
                         o.c.transactions/node  #(u.links/ui-core-node-link %2)}
   ro/columns           [m.c.transactions/tx-id
                         j.c.transactions/node
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
  {:ident         (fn [] [o.navlinks/id index-page-id])
   :initial-state (fn [_props]
                    {o.navlinks/id index-page-id
                     :ui/report    (comp/get-initial-state Report {})})
   :query         (fn []
                    [o.navlinks/id
                     {:ui/report (comp/get-query Report)}])
   :route-segment ["transactions"]
   :will-enter    (u.loader/page-loader index-page-id)}
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
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["transaction" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Transaction"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
