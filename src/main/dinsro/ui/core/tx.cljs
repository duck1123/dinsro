(ns dinsro.ui.core.tx
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.tx :as j.c.tx]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.mutations.core.tx :as mu.c.tx]
   [dinsro.ui.core.transaction-inputs :as u.c.transaction-inputs]
   [dinsro.ui.core.transaction-outputs :as u.c.transaction-outputs]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defsc RefRow
  [this {::m.c.tx/keys [fetched? id]
         :as           props}]
  {:ident         ::m.c.tx/id
   :query         [::m.c.tx/id
                   ::m.c.tx/fetched?]
   :initial-state {::m.c.tx/id       nil
                   ::m.c.tx/fetched? false}}

  (dom/tr {}
    (dom/td {} (u.links/ui-core-tx-link props))
    (dom/td {} (str fetched?))
    (dom/td {} (dom/button {:classes [:.ui.button]
                            :onClick (fn [event]
                                       (log/info :fetch-button/clicked {:event event})
                                       (comp/transact! this [(mu.c.tx/fetch! {::m.c.tx/id id})]))}
                 "Fetch"))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.c.tx/id}))

(defn ref-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "txid")
         (dom/th {} "fetched")
         (dom/th {} "Actions")))
     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-row (render-field-factory ref-row))

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [this _key]
             (let [{::m.c.tx/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.c.tx/fetch! {::m.c.tx/id id})])))})

(def override-input true)

(form/defsc-form CoreTxInput
  [this props]
  {fo/id           m.c.tx-in/id
   fo/route-prefix "tx-in"
   fo/attributes   [m.c.tx-in/coinbase
                    m.c.tx-in/txinwitness
                    m.c.tx-in/sequence
                    m.c.tx-in/txid]
   fo/title        "Input"}
  (if override-input
    (form/render-layout this props)
    (let [{::m.c.tx-in/keys [sequence txid vout script-pub-key]} props]
      (dom/div {}
        (when sequence
          (dom/p "sequence " sequence))
        (when txid
          (dom/p {} "txid: " txid))
        (when vout
          (dom/p {} "vout" vout))
        (when script-pub-key
          (dom/p {} "pub " script-pub-key))))))

(form/defsc-form CoreTxBlock
  [_this _props]
  {fo/id           m.c.blocks/id
   fo/route-prefix "tx-block"
   fo/attributes   [m.c.blocks/height m.c.blocks/hash]
   fo/title        "Block"})

(def override-form false)

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

(defn search-control-action
  [this]
  (let [props                              (comp/props this)
        {:ui/keys [controls current-rows]} props
        [current-row]                      current-rows
        values                             (map (fn [control]
                                                  (let [control-id (::control/id control)]
                                                    (log/debug :mapping {:control-id control-id})
                                                    (when (= control-id ::tx-id)
                                                      (::control/value control))))
                                                controls)
        txid-value                         (first (filter identity values))
        block-id                           nil]
    (log/info :tx/searching {:props       props
                             :current-row current-row
                             :txid-value  txid-value
                             :values      values})
    (comp/transact! this
                    [(mu.c.tx/search!
                      {::m.c.tx/block block-id
                       ::m.c.tx/tx-id txid-value})])
    (control/run! this)))

(def search-control
  {:type   :button
   :label  "Search"
   :action search-control-action})

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

(def ui-tx-report (comp/factory Report))
