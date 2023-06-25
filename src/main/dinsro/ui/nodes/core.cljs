(ns dinsro.ui.nodes.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]))

(def log-item-props false)

(defsc BodyItem
  [_this {::m.c.nodes/keys [name host network]
          :as              props}]
  {:ident         ::m.c.nodes/id
   :initial-state {::m.c.nodes/id      nil
                   ::m.c.nodes/name    ""
                   ::m.c.nodes/host    ""
                   ::m.c.nodes/network {}}
   :query         [::m.c.nodes/id
                   ::m.c.nodes/name
                   ::m.c.nodes/host
                   {::m.c.nodes/network (comp/get-query u.links/CoreNodeLinkForm)}]}
  (dom/div :.ui.item
    (dom/div {} (str name))
    (dom/div {} (str host))
    (dom/div {} (u.links/ui-network-link network))
    (when log-item-props
      (u.debug/log-props props))))

(def ui-body-item (comp/factory BodyItem {:keyfn ::m.c.nodes/id}))

(report/defsc-report Report
  [_this props]
  {ro/column-formatters {::m.c.nodes/name    #(u.links/ui-core-node-link %3)
                         ::m.c.nodes/network #(u.links/ui-network-link %2)}
   ro/columns           [m.c.nodes/name
                         m.c.nodes/host
                         m.c.nodes/network]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Fetch" ::m.c.nodes/id mu.c.nodes/fetch!)
                         (u.buttons/row-action-button "Delete" ::m.c.nodes/id mu.c.nodes/delete!)]
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.nodes/index
   ro/title             "Core Node Report"}
  (let [{:ui/keys [current-rows]} props]
    (dom/div {}
      (dom/h2 {} "Core Nodes")
      (dom/div :.ui.items
        (map ui-body-item current-rows)))))

(def ui-report (comp/factory Report))
