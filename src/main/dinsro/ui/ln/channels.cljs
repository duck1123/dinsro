(ns dinsro.ui.ln.channels
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.ui.links :as u.links]))

(defsc RefRow
  [_this {::m.ln.channels/keys [capacity commit-fee local-balance remote-balance] :as props}]
  {:ident ::m.ln.channels/id
   :query [::m.ln.channels/id
           ::m.ln.channels/channel-point
           ::m.ln.channels/capacity
           ::m.ln.channels/commit-fee
           ::m.ln.channels/local-balance
           ::m.ln.channels/remote-balance]}
  (dom/tr {}
    (dom/td (u.links/ui-channel-link props))
    (dom/td (str capacity))
    (dom/td (str commit-fee))
    (dom/td (str local-balance))
    (dom/td (str remote-balance))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.ln.channels/id}))

(defn ref-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "Channel Point")
         (dom/th {} "Capacity")
         (dom/th {} "Commit Fee")
         (dom/th {} "Local Balance")
         (dom/th {} "Remote Balance")))
     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-row (render-field-factory ref-row))

(form/defsc-form NewChannelForm [_this _props]
  {fo/id           m.ln.channels/id
   fo/attributes   [m.ln.channels/id
                    m.ln.channels/active
                    m.ln.channels/capacity
                    m.ln.channels/chan-id
                    m.ln.channels/channel-point
                    m.ln.channels/chan-status-flags
                    m.ln.channels/close-address
                    m.ln.channels/commit-fee
                    m.ln.channels/node]
   fo/route-prefix "new-channel"
   fo/title        "New Lightning Channels"})

(report/defsc-report LNChannelsReport
  [this _props]
  {ro/columns          [m.ln.channels/id
                        m.ln.channels/channel-point
                        m.ln.channels/node]
   ro/field-formatters {::m.ln.channels/node #(u.links/ui-node-link %2)
                        ::m.ln.channels/id   #(u.links/ui-channel-link %3)}
   ro/route            "channels"
   ro/row-pk           m.ln.channels/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.channels/index
   ro/title            "Lightning Channels Report"}
  (dom/div {}
    (dom/h1 {} "Channels")
    (report/render-layout this)))
