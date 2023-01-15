(ns dinsro.ui.core.tx
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.tx :as j.c.tx]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.mutations.core.tx :as mu.c.tx]
   [dinsro.ui.core.transaction-inputs :as u.c.transaction-inputs]
   [dinsro.ui.core.transaction-outputs :as u.c.transaction-outputs]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defn fetch-action
  [report-instance {::m.c.tx/keys [id]}]
  (comp/transact! report-instance [(mu.c.tx/fetch! {::m.c.tx/id id})]))

(defn delete-action
  [report-instance {::m.c.tx/keys [id]}]
  (form/delete! report-instance ::m.c.tx/id id))

(def fetch-action-button
  {:label     "Fetch"
   :action    fetch-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(defsc ShowTransaction
  "Show a core tx"
  [this {::m.c.tx/keys [id tx-id hash fetched? block size]
         :ui/keys      [inputs outputs]
         :as           props}]
  {:route-segment ["tx" :id]
   :query         [::m.c.tx/id
                   ::m.c.tx/tx-id
                   ::m.c.tx/hash
                   ::m.c.tx/size
                   ::m.c.tx/fetched?
                   {:ui/inputs (comp/get-query u.c.transaction-inputs/SubPage)}
                   {:ui/outputs (comp/get-query u.c.transaction-outputs/SubPage)}
                   {::m.c.tx/block (comp/get-query u.links/BlockHeightLinkForm)}
                   [df/marker-table '_]]
   :initial-state {::m.c.tx/id       nil
                   ::m.c.tx/tx-id    nil
                   ::m.c.tx/hash     ""
                   ::m.c.tx/block    {}
                   ::m.c.tx/size     0
                   :ui/inputs        {}
                   :ui/outputs       {}
                   ::m.c.tx/fetched? false}
   :ident         ::m.c.tx/id
   :pre-merge     (u.links/page-merger
                   ::m.c.tx/id
                   {:ui/inputs  u.c.transaction-inputs/SubPage
                    :ui/outputs u.c.transaction-outputs/SubPage})
   :will-enter    (partial u.links/page-loader ::m.c.tx/id ::ShowTransaction)}
  (log/finer :ShowTransaction/creating {:id id :props props :this this})
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
        (dom/dd {} (dom/a {:onClick #(comp/transact! this [(mu.c.tx/fetch! {::m.c.tx/id id})])
                           :href    "#"}
                     (str fetched?)))
        (dom/dt {} "Size")
        (dom/dd {} (str size))))

    (if id
      (comp/fragment
       (when inputs (u.c.transaction-inputs/ui-sub-page inputs))
       (when outputs (u.c.transaction-outputs/ui-sub-page outputs)))
      (dom/p {} "id not set"))))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.tx/tx-id
                        j.c.tx/node
                        m.c.tx/fetched?
                        m.c.tx/block]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.tx/block #(u.links/ui-block-height-link %2)
                        ::m.c.tx/tx-id (u.links/report-link ::m.c.tx/tx-id u.links/ui-core-tx-link)
                        ::m.c.tx/node  #(u.links/ui-core-node-link %2)}
   ro/source-attribute ::m.c.tx/index
   ro/title            "Transactions"
   ro/row-actions      [fetch-action-button delete-action-button]
   ro/row-pk           m.c.tx/id
   ro/run-on-mount?    true
   ro/route            "transactions"})
