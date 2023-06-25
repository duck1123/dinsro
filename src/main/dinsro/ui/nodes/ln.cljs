(ns dinsro.ui.nodes.ln
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]))

(def log-item-props false)

(defsc BodyItem
  [_this {::m.ln.nodes/keys [name network] :as props}]
  {:ident         ::m.ln.nodes/id
   :initial-state {::m.ln.nodes/id      nil
                   ::m.ln.nodes/name    ""
                   ::m.ln.nodes/network {}}
   :query         [::m.ln.nodes/id
                   ::m.ln.nodes/name
                   {::m.ln.nodes/network (comp/get-query u.links/CoreNodeLinkForm)}]}
  (dom/div :.ui.item
    (dom/div {} (str name))
    (dom/div {} (u.links/ui-network-link network))
    (when log-item-props
      (u.debug/log-props props))))

(def ui-body-item (comp/factory BodyItem {:keyfn ::m.ln.nodes/id}))

(report/defsc-report Report
  [_this props]
  {ro/columns          [m.ln.nodes/name
                        m.ln.nodes/network
                        m.ln.info/alias-attr
                        m.ln.nodes/core-node
                        m.ln.info/color
                        m.ln.nodes/user]
   ro/control-layout   {:action-buttons [::new-node ::refresh]}
   ro/controls         {::refresh  u.links/refresh-control}
   ro/field-formatters {::m.ln.nodes/name      #(u.links/ui-node-link %3)
                        ::m.ln.nodes/network   #(u.links/ui-network-link %2)
                        ::m.ln.nodes/user      #(u.links/ui-user-link %2)
                        ::m.ln.nodes/core-node #(u.links/ui-core-node-link %2)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.nodes/index
   ro/title            "Lightning Node Report"}
  (let [{:ui/keys [current-rows]} props]
    (dom/div {}
      (dom/h2 {} "Lightning nodes")
      (dom/div :.ui.items
        (map ui-body-item current-rows)))))

(def ui-report (comp/factory Report))
