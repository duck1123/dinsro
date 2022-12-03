(ns dinsro.ui.core.blocks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.ui.core.block-transactions :as u.c.block-transactions]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(def force-fetch-button false)
(def override-form false)
(def debug-page false)

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
            (dom/button {:classes [:.ui.button]
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
   fo/route-prefix "node-tx"})

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
                      m.c.blocks/difficulty
                      m.c.blocks/merkle-root
                      m.c.blocks/nonce
                      m.c.blocks/size
                      m.c.blocks/version
                      j.c.blocks/transactions]
   fo/cancel-route   ["core-blocks"]
   fo/field-styles   {::m.c.blocks/transactions   :core-tx-table
                      ::m.c.blocks/previous-block :link
                      ::m.c.blocks/next-block     :link}
   fo/subforms       {::m.c.blocks/transactions   {fo/ui CoreBlockTxSubform}
                      ::m.c.blocks/previous-block {fo/ui u.links/BlockLinkForm}
                      ::m.c.blocks/next-block     {fo/ui u.links/BlockLinkForm}}
   fo/controls       (merge form/standard-controls
                            {::fetch              fetch-button
                             ::fetch-transactions fetch-transactions-button})
   fo/route-prefix   "block-form"
   fo/title          "Core Block"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
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

        values     (map (fn [control]
                          (let [control-id (::control/id control)]
                            (log/debug :mapping {:control-id control-id})
                            (when (= control-id ::tx-id)
                              (::control/value control))))
                        controls)
        txid-value (first (filter identity values))
        block-id   (::control/value (find-control controls ::block-id))
        network-id (::control/value (find-control controls ::network-id))
        _node-id   (second (::control/value (find-control controls ::node-id)))]
    (log/info :tx/searching {:props       props
                             :current-row current-row
                             :txid-value  txid-value
                             :values      values})
    (comp/transact! this
                    [(mu.c.blocks/search!
                      {::m.c.blocks/block   block-id
                       ::m.c.blocks/network network-id})])
    (control/run! this)))

(def search-control
  {:type   :button
   :label  "Search"
   :action search-control-action})

(defn delete-action
  [report-instance {::m.c.nodes/keys [id]}]
  (form/delete! report-instance ::m.c.blocks/id id))

(defn fetch-action
  [report-instance {::m.c.nodes/keys [id]}]
  (comp/transact! report-instance [(mu.c.blocks/fetch! {::m.c.blocks/id id})]))

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(def fetch-action-button
  {:label  "Fetch"
   :action delete-action
   :style  :fetch-button})

(s/def ::row
  (s/keys
   :req [::m.c.blocks/id
         ::m.c.blocks/hash
         ::m.c.blocks/height
         ::m.c.blocks/fetched?]))
(s/def ::row-keywords #{::m.c.blocks/id
                        ::m.c.blocks/hash
                        ::m.c.blocks/height
                        ::m.c.blocks/fetched?})
(s/def ::row2
  (s/and
   (s/every-kv
    ::row-keywords ::m.c.blocks/id)
   (s/every (fn [[k v]] (= (:id v) k)))))

(s/def ::rows (s/coll-of ::row))

(defsc ShowBlock
  "Show a core block"
  [this {::m.c.blocks/keys [id height hash previous-block next-block nonce fetched? weight network]
         :ui/keys          [transactions]
         :as               props}]
  {:route-segment ["blocks" :id]
   :query         [::m.c.blocks/id
                   ::m.c.blocks/height
                   ::m.c.blocks/hash
                   ::m.c.blocks/nonce
                   ::m.c.blocks/weight
                   ::m.c.blocks/fetched?
                   {::m.c.blocks/network (comp/get-query u.links/NetworkLinkForm)}
                   {::m.c.blocks/previous-block (comp/get-query u.links/BlockHeightLinkForm)}
                   {::m.c.blocks/next-block (comp/get-query u.links/BlockHeightLinkForm)}
                   {:ui/transactions (comp/get-query u.c.block-transactions/SubPage)}
                   [df/marker-table '_]]
   :initial-state {::m.c.blocks/id             nil
                   ::m.c.blocks/height         ""
                   ::m.c.blocks/hash           ""
                   ::m.c.blocks/previous-block {}
                   ::m.c.blocks/next-block     {}
                   ::m.c.blocks/weight         0
                   ::m.c.blocks/nonce          ""
                   ::m.c.blocks/fetched?       false
                   ::m.c.blocks/network        {}
                   :ui/transactions            {}}
   :ident         ::m.c.blocks/id
   :pre-merge     (u.links/page-merger
                   ::m.c.blocks/id
                   {:ui/transactions u.c.block-transactions/SubPage})
   :will-enter    (partial u.links/page-loader ::m.c.blocks/id ::ShowBlock)}
  (log/finer :ShowBlock/creating {:id id :props props :this this})
  (dom/div {}
    (dom/div :.ui.segment
      (dom/h1 {}
              "Block " height
              (when-not (and (not force-fetch-button) fetched?)
                (comp/fragment
                 " ("
                 (dom/a {:href    "#"
                         :onClick #(comp/transact! this [(mu.c.blocks/fetch! {::m.c.blocks/id id})])}
                   "unfetched")
                 ")")))
      (dom/dl {}
        (dom/dt {} "Hash")
        (dom/dd {} hash)
        (dom/dt {} "Weight")
        (dom/dd {} weight)
        (dom/dt {} "Nonce")
        (dom/dd {} nonce)
        (dom/dt {} "Network")
        (dom/dd {} (u.links/ui-network-link network))
        (when previous-block
          (comp/fragment
           (dom/dt {} "Previous")
           (dom/dd {} (u.links/ui-block-height-link previous-block))))
        (when next-block
          (comp/fragment
           (dom/dt {} "Next")
           (dom/dd {} (u.links/ui-block-height-link next-block)))))
      (when debug-page
        (dom/button {:onClick (fn [_e]
                                (log/info :ShowBlock/fetch-button-clicked {})
                                (comp/transact! this [(mu.c.blocks/fetch! {::m.c.blocks/id id})]))}

          "Fetch")))
    (if id
      (dom/div {}
        (u.c.block-transactions/ui-sub-page transactions))
      (dom/div {}
        (dom/p {} "No id")))))

(report/defsc-report CoreBlockReport
  [_this _props]
  {ro/columns          [m.c.blocks/hash
                        m.c.blocks/height
                        m.c.blocks/fetched?]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.blocks/hash (u.links/report-link ::m.c.blocks/hash u.links/ui-block-link)}
   ro/source-attribute ::m.c.blocks/index
   ro/title            "Core Blocks"
   ro/row-actions      [delete-action-button]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/route            "blocks"})

(def ui-blocks-report (comp/factory CoreBlockReport))

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.c.blocks/hash
                        m.c.blocks/height
                        m.c.blocks/fetched?]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.blocks/hash (u.links/report-link ::m.c.blocks/hash u.links/ui-block-link)}
   ro/source-attribute ::m.c.blocks/admin-index
   ro/title            "Admin Core Blocks"
   ro/row-actions      [delete-action-button]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/route            "blocks"})
