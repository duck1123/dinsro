(ns dinsro.ui.core.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.ui.core.transaction-inputs :as u.c.transaction-inputs]
   [dinsro.ui.core.transaction-outputs :as u.c.transaction-outputs]
   [dinsro.ui.links :as u.links]))

(defn fetch-action
  [report-instance {::m.c.transactions/keys [id]}]
  (comp/transact! report-instance [(mu.c.transactions/fetch! {::m.c.transactions/id id})]))

(defn delete-action
  [report-instance {::m.c.transactions/keys [id]}]
  (form/delete! report-instance ::m.c.transactions/id id))

(def fetch-action-button
  {:label     "Fetch"
   :action    fetch-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(defsc Show
  "Show a core tx"
  [this {::m.c.transactions/keys [id tx-id hash fetched? block size]
         :ui/keys                [inputs outputs]}]
  {:route-segment ["tx" :id]
   :query         [::m.c.transactions/id
                   ::m.c.transactions/tx-id
                   ::m.c.transactions/hash
                   ::m.c.transactions/size
                   ::m.c.transactions/fetched?
                   {:ui/inputs (comp/get-query u.c.transaction-inputs/SubPage)}
                   {:ui/outputs (comp/get-query u.c.transaction-outputs/SubPage)}
                   {::m.c.transactions/block (comp/get-query u.links/BlockHeightLinkForm)}
                   [df/marker-table '_]]
   :initial-state {::m.c.transactions/id       nil
                   ::m.c.transactions/tx-id    nil
                   ::m.c.transactions/hash     ""
                   ::m.c.transactions/block    {}
                   ::m.c.transactions/size     0
                   :ui/inputs                  {}
                   :ui/outputs                 {}
                   ::m.c.transactions/fetched? false}
   :ident         ::m.c.transactions/id
   :pre-merge     (u.links/page-merger
                   ::m.c.transactions/id
                   {:ui/inputs  u.c.transaction-inputs/SubPage
                    :ui/outputs u.c.transaction-outputs/SubPage})
   :will-enter    (partial u.links/page-loader ::m.c.transactions/id ::Show)}
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
       (when inputs ((comp/factory u.c.transaction-inputs/SubPage) inputs))
       (when outputs ((comp/factory u.c.transaction-outputs/SubPage) outputs)))
      (dom/p {} "id not set"))))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.transactions/tx-id
                        j.c.transactions/node
                        m.c.transactions/fetched?
                        m.c.transactions/block]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {::m.c.transactions/block #(u.links/ui-block-height-link %2)
                        ::m.c.transactions/tx-id (u.links/report-link ::m.c.transactions/tx-id u.links/ui-core-tx-link)
                        ::m.c.transactions/node  #(u.links/ui-core-node-link %2)}
   ro/route            "transactions"
   ro/row-actions      [fetch-action-button delete-action-button]
   ro/row-pk           m.c.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.transactions/index
   ro/title            "Transactions"})
