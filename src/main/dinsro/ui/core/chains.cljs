(ns dinsro.ui.core.chains
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.chains :as j.c.chains]
   [dinsro.menus :as me]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.ui.core.chains.networks :as u.c.c.networks]
   [dinsro.ui.links :as u.links]))

(def override-form false)

(form/defsc-form NewForm
  [this props]
  {fo/attributes     [m.c.chains/name]
   fo/cancel-route   ["chains"]
   fo/id             m.c.chains/id
   fo/route-prefix   "chain"
   fo/title          "Chain"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} "foo")
      (form/render-layout this props))))

(defrouter Router
  [_this _props]
  {:router-targets [u.c.c.networks/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.c.chains/keys [id name]
          :ui/keys          [router]
          :as               props}]
  {:ident         ::m.c.chains/id
   :initial-state {::m.c.chains/id   nil
                   ::m.c.chains/name ""
                   :ui/router        {}}
   :pre-merge     (u.links/page-merger ::m.c.chains/id {:ui/router Router})
   :query         [::m.c.chains/id
                   ::m.c.chains/name
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["chain" :id]
   :will-enter    (partial u.links/page-loader ::m.c.chains/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} "Show Chain")
     (dom/dl {}
       (dom/dt {} "Name")
       (dom/dd {} (str name))))
   (u.links/ui-nav-menu {:id id :menu-items me/core-chains-menu-items})
   (if router
     (ui-router router)
     (dom/div :.ui.segment
       (dom/h3 {} "Chain Router not loaded")
       (u.links/ui-props-logger props)))))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.chains/name #(u.links/ui-chain-link %3)}
   ro/columns           [m.c.chains/name]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "chains"
   ro/row-pk            m.c.chains/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.chains/index
   ro/title             "Chains"})
