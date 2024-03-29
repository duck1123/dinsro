(ns dinsro.ui.reports.home.core-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.collections.table.ui-table :refer [ui-table]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-body :refer [ui-table-body]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header :refer [ui-table-header]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header-cell :refer [ui-table-header-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.ui.links :as u.links]))

;; [[../../../joins/core/nodes.cljc]]
;; [[../../../model/core/nodes.cljc]]

(def model-key ::m.c.nodes/id)

(def show-item? false)
(def override-report false)

(defsc BodyItem-Item
  [_this {::m.c.nodes/keys [network] :as props}]
  {:ident         ::m.c.nodes/id
   :initial-state {::m.c.nodes/id      nil
                   ::m.c.nodes/name    ""
                   ::m.c.nodes/network {}}
   :query         [::m.c.nodes/id
                   ::m.c.nodes/name
                   {::m.c.nodes/network (comp/get-query u.links/NetworkLinkForm)}]}
  (dom/div :.ui.item
    (dom/div {} (u.links/ui-core-node-link props))
    (dom/div {} (u.links/ui-network-link network))))

(def ui-body-item-item (comp/factory BodyItem-Item {:keyfn model-key}))

(defsc BodyItem-Table
  [_this {::m.c.nodes/keys [network]
          :as              props}]
  {:ident         ::m.c.nodes/id
   :initial-state {::m.c.nodes/id      nil
                   ::m.c.nodes/name    ""
                   ::m.c.nodes/network {}}
   :query         [::m.c.nodes/id
                   ::m.c.nodes/name
                   {::m.c.nodes/network (comp/get-query u.links/NetworkLinkForm)}]}
  (ui-table-row {}
    (ui-table-cell {} (u.links/ui-core-node-link props))
    (ui-table-cell {} (u.links/ui-network-link network))))

(def BodyItem BodyItem-Table)

(def ui-body-item-table (comp/factory BodyItem-Table {:keyfn ::m.c.nodes/id}))
(def ui-body-item (if show-item? ui-body-item-item ui-body-item-table))

(defsc ReportBody-List
  [_this props]
  (let [{:ui/keys [current-rows]} props]
    (ui-segment {}
      (dom/h2 {} "Core Nodes")
      (dom/div :.ui.items
        (map ui-body-item current-rows)))))

(defsc ReportBody-Table
  [_this props]
  (let [{:ui/keys [current-rows]} props]
    (ui-segment {}
      (dom/h2 {} "Core Nodes")
      (ui-table {}
        (ui-table-header {}
          (ui-table-row {}
            (ui-table-header-cell {} "Name")
            (ui-table-header-cell {} "Network")))
        (ui-table-body {}
          (map ui-body-item-table current-rows))))))

(def use-table? true)
(def ReportBody (if use-table? ReportBody-Table ReportBody-List))

(def ui-report-body (comp/factory ReportBody))

(report/defsc-report Report
  [this props]
  {ro/BodyItem BodyItem
   ro/column-formatters {::m.c.nodes/name    #(u.links/ui-core-node-link %3)
                         ::m.c.nodes/network #(u.links/ui-network-link %2)}
   ro/columns           [m.c.nodes/name
                         m.c.nodes/host
                         m.c.nodes/network]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.nodes/index
   ro/title             "Core Nodes"}
  (if override-report
    (report/render-layout this)
    (ui-report-body props)))

(def ui-report (comp/factory Report))
