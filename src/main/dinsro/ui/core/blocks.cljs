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
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defsc RefRow
  [this {::m.c.blocks/keys [fetched? height id] :as props}]
  {:ident ::m.c.blocks/id
   :query [::m.c.blocks/id
           ::m.c.blocks/fetched?
           ::m.c.blocks/hash
           ::m.c.blocks/height]}
  (dom/tr {}
    (dom/td (str fetched?))
    (dom/td (u.links/ui-block-link props))
    (dom/td (str height))
    (dom/td {}
            (dom/button
              {:classes [:.ui.button]
               :onClick (fn [event]
                          (log/info :fetch-button/clicked {:event event})
                          (comp/transact! this [(mu.c.blocks/fetch! {::m.c.blocks/id id})]))}
              "Fetch"))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.c.blocks/id}))

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
  {fo/id           m.c.blocks/id
   fo/route-prefix "ln-core-block"
   fo/title        "Block"
   fo/attributes   [m.c.blocks/fetched?
                    m.c.blocks/hash
                    m.c.blocks/height]})

(declare CoreBlockForm)

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [this _key]
             (let [{::m.c.blocks/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.c.blocks/fetch! {::m.c.blocks/id id})])
               (form/view! this CoreBlockForm id)))})

(def fetch-transactions-button
  {:type   :button
   :local? true
   :label  "Fetch Transactions"
   :action (fn [this _key]
             (let [{::m.c.blocks/keys [id]} (comp/props this)]
               (comp/transact!
                this
                [(mu.c.blocks/fetch-transactions! {::m.c.blocks/id id})])
               (form/view! this CoreBlockForm id)))})

(form/defsc-form CoreBlockTxSubform
  [_this _props]
  {fo/id           m.c.tx/id
   fo/title        "Core Block Transactions"
   fo/attributes   [m.c.tx/tx-id m.c.tx/fetched?]
   fo/route-prefix "core-node-tx"})

(def override-form false)

(form/defsc-form CoreBlockForm
  [this props]
  {fo/id             m.c.blocks/id
   fo/action-buttons (concat [::fetch ::fetch-transactions] form/standard-action-buttons)
   fo/attributes     [m.c.blocks/hash
                      m.c.blocks/previous-block
                      m.c.blocks/next-block
                      m.c.blocks/fetched?
                      m.c.blocks/height
                      m.c.blocks/bits
                      m.c.blocks/chainwork
                      m.c.blocks/node
                      m.c.blocks/difficulty
                      m.c.blocks/merkle-root
                      m.c.blocks/nonce
                      m.c.blocks/size
                      m.c.blocks/version
                      j.c.blocks/transactions]
   fo/cancel-route   ["core-blocks"]
   fo/field-styles   {::m.c.blocks/transactions   :core-tx-table
                      ::m.c.blocks/node           :link
                      ::m.c.blocks/previous-block :link
                      ::m.c.blocks/next-block     :link}
   fo/subforms       {::m.c.blocks/node           {fo/ui u.links/CoreNodeLinkForm}
                      ::m.c.blocks/transactions   {fo/ui CoreBlockTxSubform}
                      ::m.c.blocks/previous-block {fo/ui u.links/BlockLinkForm}
                      ::m.c.blocks/next-block     {fo/ui u.links/BlockLinkForm}}
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
                    [(mu.c.blocks/search!
                      {::m.c.blocks/block block-id
                       ::m.c.blocks/node  node-id})])
    (control/run! this)))

(def search-control
  {:type   :button
   :label  "Search"
   :action search-control-action})

(defn delete-action
  [report-instance {::m.c.nodes/keys [id]}]
  (form/delete! report-instance ::m.c.blocks/id id))

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(report/defsc-report CoreBlockReport
  [_this _props]
  {ro/columns  [m.c.blocks/hash
                m.c.blocks/height
                m.c.blocks/fetched?
                m.c.blocks/node]
   ro/controls {::search  search-control
                ::refresh {:type   :button
                           :label  "Refresh"
                           :action (fn [this] (control/run! this))}
                ::node-id {:type          :picker
                           :style         :default
                           :default-value ""
                           :label         "Node ID"
                           ::picker-options/query-key       ::m.c.nodes/index
                           ::picker-options/query-component u.links/CoreNodeLinkForm
                           ::picker-options/options-xform
                           (fn [_ options]
                             (mapv
                              (fn [{::m.c.nodes/keys [id name]}]
                                {:text  (str name)
                                 :value [::m.c.nodes/id id]})
                              (sort-by ::m.c.nodes/name options)))}

                ::block-id {:type          :string
                            :style         :search
                            :default-value ""
                            :label         "Block ID"
                            :onChange      (fn [this _] (control/run! this))}}
   ro/control-layout   {:inputs         [[::block-id ::node-id ::search]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.c.blocks/node (fn [_ props] (u.links/ui-core-node-link props))}
   ;; fo/field-options
   ;; {::node-id
   ;;  {

   ;;   ::picker-options/query-key       ::m.c.nodes/index
   ;;   ::picker-options/query-component u.links/CoreNodeLinkForm
   ;;   ::picker-options/options-xform
   ;;   (fn [_ options]
   ;;     (mapv
   ;;      (fn [{::m.c.nodes/keys [id name]}]
   ;;        {:text  (str name)
   ;;         :value [::m.c.nodes/id id]})
   ;;      (sort-by ::m.c.nodes/name options)))

   ;;   }}
   ro/form-links       {::m.c.blocks/hash CoreBlockForm}
   ro/source-attribute ::m.c.blocks/index
   ro/title            "Core Blocks"
   ro/row-actions [delete-action-button]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/route            "core-blocks"})
