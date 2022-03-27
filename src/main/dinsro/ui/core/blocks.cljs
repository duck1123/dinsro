(ns dinsro.ui.core.blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.blocks :as j.core-block]
   [dinsro.model.core.blocks :as m.core-block]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.tx :as m.core-tx]
   [dinsro.mutations.core.blocks :as mu.core-blocks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defsc RefRow
  [this {::m.core-block/keys [fetched? height id] :as props}]
  {:ident ::m.core-block/id
   :query [::m.core-block/id
           ::m.core-block/fetched?
           ::m.core-block/hash
           ::m.core-block/height]}
  (dom/tr {}
    (dom/td (str fetched?))
    (dom/td (u.links/ui-block-link props))
    (dom/td (str height))
    (dom/td {}
            (dom/button
              {:classes [:.ui.button]
               :onClick (fn [event]
                          (log/info :fetch-button/clicked {:event event})
                          (comp/transact! this [(mu.core-blocks/fetch! {::m.core-block/id id})]))}
              "Fetch"))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.core-block/id}))

(defn ref-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "Fetched")
         (dom/th {} "Hash")
         (dom/th {} "Height")))
     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-row (render-field-factory ref-row))

(form/defsc-form CoreBlockSubForm
  [_this _props]
  {fo/id           m.core-block/id
   fo/route-prefix "ln-core-block"
   fo/title        "Block"
   fo/attributes   [m.core-block/fetched?
                    m.core-block/hash
                    m.core-block/height]})

(declare CoreBlockForm)

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [this _key]
             (let [{::m.core-block/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.core-blocks/fetch! {::m.core-block/id id})])
               (form/view! this CoreBlockForm id)))})

(def fetch-transactions-button
  {:type   :button
   :local? true
   :label  "Fetch Transactions"
   :action (fn [this _key]
             (let [{::m.core-block/keys [id]} (comp/props this)]
               (comp/transact!
                this
                [(mu.core-blocks/fetch-transactions! {::m.core-block/id id})])
               (form/view! this CoreBlockForm id)))})

(form/defsc-form CoreBlockTxSubform
  [_this _props]
  {fo/id           m.core-tx/id
   fo/title        "Core Block Transactions"
   fo/attributes   [m.core-tx/tx-id m.core-tx/fetched?]
   fo/route-prefix "core-node-tx"})

(def override-form false)

(form/defsc-form CoreBlockForm
  [this props]
  {fo/id             m.core-block/id
   fo/action-buttons (concat [::fetch ::fetch-transactions] form/standard-action-buttons)
   fo/attributes     [m.core-block/hash
                      m.core-block/previous-block
                      m.core-block/next-block
                      m.core-block/fetched?
                      m.core-block/height
                      m.core-block/bits
                      m.core-block/chainwork
                      m.core-block/node
                      m.core-block/difficulty
                      m.core-block/merkle-root
                      m.core-block/nonce
                      m.core-block/size
                      m.core-block/version
                      j.core-block/transactions]
   fo/cancel-route   ["core-blocks"]
   fo/field-styles   {::m.core-block/transactions   :core-tx-table
                      ::m.core-block/node           :link
                      ::m.core-block/previous-block :link
                      ::m.core-block/next-block     :link}
   fo/subforms       {::m.core-block/node           {fo/ui u.links/CoreNodeLinkForm}
                      ::m.core-block/transactions   {fo/ui CoreBlockTxSubform}
                      ::m.core-block/previous-block {fo/ui u.links/BlockLinkForm}
                      ::m.core-block/next-block     {fo/ui u.links/BlockLinkForm}}
   fo/controls       (merge form/standard-controls
                            {::fetch fetch-button
                             ::fetch-transactions fetch-transactions-button})
   fo/route-prefix   "core-block"
   fo/title          "Core Block"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      "foo"
      (form/render-layout this props))))

(defn find-control
  [controls key]
  (->> controls
       (map (fn [control]
              (let [control-id (::control/id control)]
                (log/debug :mapping {:control-id control-id})
                (when (= control-id key) control))))
       (filter identity)
       first))

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
        block-id                           (::control/value (find-control controls ::block-id))
        node-id                            (second (::control/value (find-control controls ::node-id)))]
    (log/info :tx/searching {:props       props
                             :current-row current-row
                             :txid-value  txid-value
                             :values      values})
    (comp/transact! this
                    [(mu.core-blocks/search!
                      {::m.core-block/block block-id
                       ::m.core-block/node  node-id})])
    (control/run! this)))

(def search-control
  {:type   :button
   :label  "Search"
   :action search-control-action})

(defn delete-action
  [report-instance {::m.core-nodes/keys [id]}]
  (form/delete! report-instance ::m.core-block/id id))

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(report/defsc-report CoreBlockReport
  [_this _props]
  {ro/columns  [m.core-block/hash
                m.core-block/height
                m.core-block/fetched?
                m.core-block/node]
   ro/controls {::search  search-control
                ::refresh {:type   :button
                           :label  "Refresh"
                           :action (fn [this] (control/run! this))}
                ::node-id {:type          :picker
                           :style         :default
                           :default-value ""
                           :label         "Node ID"
                           ::picker-options/query-key       ::m.core-nodes/index
                           ::picker-options/query-component u.links/CoreNodeLinkForm
                           ::picker-options/options-xform
                           (fn [_ options]
                             (mapv
                              (fn [{::m.core-nodes/keys [id name]}]
                                {:text  (str name)
                                 :value [::m.core-nodes/id id]})
                              (sort-by ::m.core-nodes/name options)))}

                ::block-id {:type          :string
                            :style         :search
                            :default-value ""
                            :label         "Block ID"
                            :onChange      (fn [this _] (control/run! this))}}
   ro/control-layout   {:inputs         [[::block-id ::node-id ::search]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.core-block/node (fn [_ props] (u.links/ui-core-node-link props))}
   ;; fo/field-options
   ;; {::node-id
   ;;  {

   ;;   ::picker-options/query-key       ::m.core-nodes/index
   ;;   ::picker-options/query-component u.links/CoreNodeLinkForm
   ;;   ::picker-options/options-xform
   ;;   (fn [_ options]
   ;;     (mapv
   ;;      (fn [{::m.core-nodes/keys [id name]}]
   ;;        {:text  (str name)
   ;;         :value [::m.core-nodes/id id]})
   ;;      (sort-by ::m.core-nodes/name options)))

   ;;   }}
   ro/form-links       {::m.core-block/hash CoreBlockForm}
   ro/source-attribute ::m.core-block/index
   ro/title            "Core Blocks"
   ro/row-actions [delete-action-button]
   ro/row-pk           m.core-block/id
   ro/run-on-mount?    true
   ro/route            "core-blocks"})
