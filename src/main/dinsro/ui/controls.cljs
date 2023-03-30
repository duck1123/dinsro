(ns dinsro.ui.controls
  (:require
   ["victory" :as victory]
   [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.rendering.semantic-ui.semantic-ui-controls :as sui]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(def ui-victory-chart (interop/react-factory victory/VictoryChart))
(def ui-victory-line (interop/react-factory victory/VictoryLine))

(def matchers
  {::m.accounts/id u.links/ui-account-link
   ::m.c.peers/id  u.links/ui-core-peer-link
   ::m.c.words/id  u.links/ui-word-link})

(defn get-matcher
  [value]
  (->> matchers
       (map (fn [[kw f]] (when (get value kw) f)))
       (filter identity)
       first))

(defn link-control
  [{:keys [value] :as env} _attribute]
  (let [{address-id     ::m.c.wallet-addresses/id
         category-id    ::m.categories/id
         block-id       ::m.c.blocks/id
         core-node-id   ::m.c.nodes/id
         core-tx-id     ::m.c.transactions/id
         currency-id    ::m.currencies/id
         channel-id     ::m.ln.channels/id
         invoice-id     ::m.ln.invoices/id
         payreq-id      ::m.ln.payreqs/id
         payment-id     ::m.ln.payments/id
         peer-id        ::m.ln.peers/id
         node-id        ::m.ln.nodes/id
         source-id      ::m.rate-sources/id
         transaction-id ::m.transactions/id
         user-id        ::m.users/id
         wallet-id      ::m.c.wallets/id} value]
    (or
     (when-let [matcher (get-matcher value)]
       (log/debug :link-control/matched {:matcher matcher :value value})
       (matcher value))
     (when block-id (u.links/ui-block-link value))
     (when core-tx-id (u.links/ui-core-tx-link value))
     (when category-id (u.links/ui-category-link value))
     (when channel-id (u.links/ui-channel-link value))
     (when core-node-id (u.links/ui-core-node-link value))
     (when currency-id (u.links/ui-currency-link value))
     (when invoice-id (u.links/ui-invoice-link value))
     (when node-id (u.links/ui-node-link value))
     (when payment-id (u.links/ui-payment-link value))
     (when payreq-id (u.links/ui-payreq-link value))
     (when peer-id (u.links/ui-ln-peer-link value))
     (when source-id (u.links/ui-rate-source-link value))
     (when transaction-id (u.links/ui-transaction-link value))
     (when user-id (u.links/ui-user-link value))
     (when wallet-id (u.links/ui-wallet-link value))
     (when address-id (u.links/ui-wallet-address-link value))
     (dom/div (merge env {}) (str "link control: " value)))))

(def render-link-control (render-field-factory link-control))

(defsc LinkControlItem
  [_this {:keys [attribute item]}]
  (dom/div {}
    (link-control {:value item} attribute)))

(def ui-link-control-list-item
  (comp/factory
   LinkControlItem
   {:keyfn (fn [i]
             (let [ident (get-in i
                                 [:item
                                  :com.fulcrologic.fulcro.algorithms.form-state/config
                                  :com.fulcrologic.fulcro.algorithms.form-state/id])]
               (second ident)))}))

(defn link-list-control
  [{:keys [value]} attribute]
  (dom/div {}
    (for [item value] (ui-link-control-list-item {:attribute attribute :item item}))))

(def render-link-list-control (render-field-factory link-list-control))

(defn ref-control
  [{:keys [value]} _attribute]
  (dom/div :.ui
    (dom/div {} "default ref control: ")
    (dom/pre
     {}
     (dom/code {} (pr-str value)))))

(def render-ref (render-field-factory ref-control))

(defn date-control
  [{:keys [value] :as env} attribute]
  (log/info :date-control/starting {:env env :attribute attribute})
  (let [this nil
        iso-string (.toISOString value)
        date-string (.substring iso-string 0 10)
        time-string (.substring iso-string 11 19)]
    (dom/div {}
      (ui-form-field {}
        (dom/div {} (str value))
        (dom/div {} (str iso-string))
        (dom/div {} (str date-string))
        (dom/div {} (str time-string))
        (ui-form-input
         {:value    date-string
          :type     "date"
          :onChange (fn [evt _] (fm/set-string! this :user/username :event evt))})
        (ui-form-input
         {:value    time-string
          :type     "time"
          :onChange (fn [evt _] (fm/set-string! this :user/username :event evt))})))))

(def render-date (render-field-factory date-control))

(defn uuid-control
  [{:keys [value]} _attribute]
  (dom/div {} (str "uuid control" value)))

(def render-uuid (render-field-factory uuid-control))

(defn user-selector-control
  [{:keys [value]} _attribute]
  (dom/div {} (str "user selector control" value)))

(def render-user-selector (render-field-factory user-selector-control))

(defn control-type
  [controls type style control]
  (assoc-in controls [::form/type->style->control type style] control))

(defn rate-chart-control
  [{:keys [value]} _attribute]
  (dom/div {}
    (ui-victory-chart
     {:domainPadding {:x 50}}
     (ui-victory-line
      {:data   value
       :style  (clj->js {:data {:stroke "#c43a31"}})
       :labels (fn [v] (comp/isoget-in v ["date" "rate"]))
       :x      "date"
       :y      "rate"}))))

(def render-rate-chart-control (render-field-factory rate-chart-control))

(defsc UUIDControl
  [_this {:keys [control-key instance]}]
  (let [props (comp/props instance)
        id    (get-in props [:ui/parameters control-key])]
    (log/fine :uuid/render {:id id :control-key control-key})
    (dom/div {})))

(def uuid-control-render (comp/factory UUIDControl {:keyfn :control-key}))

(defn all-controls
  []
  (-> sui/all-controls
      (control-type :ref  :default          render-ref)
      (control-type :ref  :link             render-link-control)
      (control-type :ref  :link-list        render-link-list-control)
      (control-type :ref  :rate-chart       render-rate-chart-control)
      (control-type :ref  :user-selector    render-user-selector)
      (control-type :ref  :word-list        u.c.wallets/render-word-list)
      (control-type :uuid :default          render-uuid)
      (control-type :date :default          render-date)
      (assoc-in [::control/type->style->control :uuid :default] uuid-control-render)))
