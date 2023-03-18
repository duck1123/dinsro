(ns dinsro.ui.core.chains
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.ui.core.chain-networks :as u.c.chain-networks]
   [dinsro.ui.links :as u.links]))

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

(defrouter Router
  [_this _props]
  {:router-targets [u.c.chain-networks/SubPage]})

(def ui-router (comp/factory Router))

(def menu-items
  [{:key   "networks"
    :name  "Networks"
    :route "dinsro.ui.core.chain-networks/SubPage"}])

(defsc ShowChain
  [_this {::m.c.chains/keys [id name]
          :ui/keys          [router]
          :as               props}]
  {:ident         ::m.c.chains/id
   :query         [::m.c.chains/id
                   ::m.c.chains/name
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.c.chains/id   nil
                   ::m.c.chains/name ""
                   :ui/router        {}}
   :route-segment ["chain" :id]
   :pre-merge     (u.links/page-merger ::m.c.chains/id {:ui/router Router})
   :will-enter    (partial u.links/page-loader ::m.c.chains/id ::ShowChain)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} "Show Chain")
     (dom/dl {}
       (dom/dt {} "Name")
       (dom/dd {} (str name))))
   (u.links/ui-nav-menu {:id id :menu-items menu-items})
   (if router
     (ui-router router)
     (dom/div :.ui.segment
       (dom/h3 {} "Chain Router not loaded")
       (u.links/ui-props-logger props)))))

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

