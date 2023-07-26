(ns dinsro.ui.home.ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.links :as u.links]))

;; [[../../joins/ln/nodes.cljc]]
;; [[../../model/ln/nodes.cljc]]

(def model-key ::m.ln.nodes/id)

(def override-ln-nodes-report false)

(defsc BodyItem
  [_this props]
  {:ident         ::m.ln.nodes/id
   :initial-state {::m.ln.nodes/id        nil
                   ::m.ln.nodes/name      ""
                   ::m.ln.nodes/network   {}
                   ::m.ln.info/alias-attr ""
                   ::m.ln.info/color      ""}
   :query         [::m.ln.nodes/id
                   ::m.ln.nodes/name
                   ::m.ln.nodes/network
                   ::m.ln.info/alias-attr
                   ::m.ln.info/color]}
  (dom/div :.ui.item
    (u.links/ui-node-link props)))

(def ui-body-item (comp/factory BodyItem {:keyfn ::m.ln.nodes/id}))

(report/defsc-report Report
  [this props]
  {ro/BodyItem BodyItem
   ro/column-formatters {::m.ln.nodes/name      #(u.links/ui-node-link %3)
                         ::m.ln.nodes/network   #(u.links/ui-network-link %2)
                         ::m.ln.nodes/user      #(u.links/ui-user-link %2)
                         ::m.ln.nodes/core-node #(u.links/ui-core-node-link %2)}
   ro/columns           [m.ln.nodes/name
                         m.ln.nodes/network
                         m.ln.info/alias-attr
                         m.ln.info/color]
   ro/control-layout    {:action-buttons [::new-node ::refresh]}
   ro/controls          {::refresh  u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.nodes/index
   ro/title             "Lightning Nodes"}
  (if override-ln-nodes-report
    (report/render-layout this)
    (let [{:ui/keys [current-rows]} props]
      (ui-segment {}
        (dom/h2 {} "Lightning Nodes")
        (dom/div :.ui.items
          (map ui-body-item current-rows))))))

(def ui-report (comp/factory Report))
