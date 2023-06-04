(ns dinsro.ui.admin.core.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.core.transactions.inputs :as u.c.t.inputs]
   [dinsro.ui.core.transactions.outputs :as u.c.t.outputs]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(defsc Show
  "Show a core tx"
  [this {::m.c.transactions/keys [id tx-id hash fetched? block size]
         :ui/keys                [inputs outputs]}]
  {:ident         ::m.c.transactions/id
   :initial-state {::m.c.transactions/id       nil
                   ::m.c.transactions/tx-id    nil
                   ::m.c.transactions/hash     ""
                   ::m.c.transactions/block    {}
                   ::m.c.transactions/size     0
                   :ui/inputs                  {}
                   :ui/outputs                 {}
                   ::m.c.transactions/fetched? false}
   :pre-merge     (u.loader/page-merger
                   ::m.c.transactions/id
                   {:ui/inputs  [u.c.t.inputs/SubPage {}]
                    :ui/outputs [u.c.t.outputs/SubPage {}]})
   :query         [::m.c.transactions/id
                   ::m.c.transactions/tx-id
                   ::m.c.transactions/hash
                   ::m.c.transactions/size
                   ::m.c.transactions/fetched?
                   {:ui/inputs (comp/get-query u.c.t.inputs/SubPage)}
                   {:ui/outputs (comp/get-query u.c.t.outputs/SubPage)}
                   {::m.c.transactions/block (comp/get-query u.links/BlockHeightLinkForm)}
                   [df/marker-table '_]]
   :route-segment ["tx" :id]
   :will-enter    (partial u.loader/page-loader ::m.c.transactions/id ::Show)}
  (dom/div {}
    (dom/div :.ui.segment
      (dom/h1 {} "Transaction")
      (dom/dl {}
        (dom/dt {} "TX id")
        (dom/dd {} (str tx-id))
        (dom/dt {} "Hash: ")
        (dom/dd {} (str hash))
        (dom/dt {} "Block: ")
        (dom/dd {} (u.links/ui-block-height-link block))
        (dom/dt {} "Fetched")
        (dom/dd {} (dom/a {:onClick #(comp/transact! this [(mu.c.transactions/fetch! {::m.c.transactions/id id})])
                           :href    "#"}
                     (str fetched?)))
        (dom/dt {} "Size")
        (dom/dd {} (str size))))
    (if id
      (comp/fragment
       (when inputs ((comp/factory u.c.t.inputs/SubPage) inputs))
       (when outputs ((comp/factory u.c.t.outputs/SubPage) outputs)))
      (dom/p {} "id not set"))))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.transactions/block #(u.links/ui-block-height-link %2)
                         ::m.c.transactions/tx-id #(u.links/ui-core-tx-link %3)}
   ro/columns           [m.c.transactions/tx-id
                         m.c.transactions/fetched?
                         m.c.transactions/block]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "transactions"
   ro/row-actions       [(u.buttons/row-action-button "Fetch" ::m.c.transactions/id mu.c.transactions/fetch!)
                         (u.buttons/row-action-button "Delete" ::m.c.transactions/id mu.c.transactions/delete!)]
   ro/row-pk            m.c.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.transactions/index
   ro/title             "Transactions"})
