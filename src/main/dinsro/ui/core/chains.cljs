(ns dinsro.ui.core.chains
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.ui.core.chain-networks :as u.c.chain-networks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def override-form false)

(form/defsc-form CoreChainForm
  [this props]
  {fo/id             m.c.chains/id
   fo/attributes     [m.c.chains/name]
   fo/cancel-route   ["chains"]
   fo/route-prefix   "chain"
   fo/title          "Chain"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} "foo")
      (form/render-layout this props))))

(defsc ShowChain
  [_this {::m.c.chains/keys [name]
          :ui/keys          [networks]
          :as               props}]
  {:route-segment ["chain" :id]
   :query         [::m.c.chains/id
                   ::m.c.chains/name
                   {:ui/networks (comp/get-query u.c.chain-networks/SubPage)}
                   [df/marker-table '_]]
   :initial-state {::m.c.chains/id    nil
                   ::m.c.chains/name  ""
                   :ui/networks {}}
   :ident         ::m.c.chains/id
   :pre-merge     (u.links/page-merger
                   ::m.c.chains/id
                   {:ui/networks u.c.chain-networks/SubPage})
   :will-enter    (partial u.links/page-loader ::m.c.chains/id ::ShowChain)}
  (log/info :ShowChain/starting {:props props})
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} (str "Show Chain: " name)))
   (if networks
     (u.c.chain-networks/ui-sub-page networks)
     (dom/p :.ui.segment "Failed to load chain networks"))))

(def ui-show-chain (comp/factory ShowChain))

(report/defsc-report Report
  [_this _props]
  {ro/columns           [m.c.chains/name]
   ro/column-formatters {::m.c.chains/name #(u.links/ui-chain-link %3)}
   ro/controls          {::refresh u.links/refresh-control}
   ro/control-layout    {:action-buttons [::refresh]}
   ro/source-attribute  ::m.c.chains/index
   ro/title             "Chains"
   ro/row-pk            m.c.chains/id
   ro/run-on-mount?     true
   ro/route             "chains"})

(def ui-tx-report (comp/factory Report))
