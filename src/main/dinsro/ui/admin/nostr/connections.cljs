(ns dinsro.ui.admin.nostr.connections
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.menus :as me]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.ui.links :as u.links]))

(defsc Show
  [_this {::m.n.connections/keys [id status relay start-time end-time]}]
  {:ident         ::m.n.connections/id
   :initial-state {::m.n.connections/id         nil
                   ::m.n.connections/status     :unknown
                   ::m.n.connections/relay      {}
                   ::m.n.connections/start-time nil
                   ::m.n.connections/end-time   nil}
   :query         [::m.n.connections/id
                   ::m.n.connections/status
                   {::m.n.connections/relay (comp/get-query u.links/RelayLinkForm)}
                   ::m.n.connections/start-time
                   ::m.n.connections/end-time]
   :route-segment ["connections" :id]
   :will-enter    (partial u.links/page-loader ::m.n.connections/id ::Show)}
  (dom/div {}
    (dom/div :.ui.segment
      (dom/div {} (str id))
      (dom/div {} (name status))
      (dom/div {} (u.links/ui-relay-link relay))
      (dom/div {} (str start-time))
      (dom/div {} (str end-time)))
    (u.links/ui-nav-menu {:menu-items me/admin-nostr-connections-menu-items :id id})))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.connections/status #(u.links/ui-connection-link %3)
                         ::m.n.connections/relay  #(u.links/ui-relay-link %2)}
   ro/columns           [m.n.connections/status
                         m.n.connections/relay
                         m.n.connections/start-time
                         m.n.connections/end-time]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/route             "connections"
   ro/row-pk            m.n.connections/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.connections/index
   ro/title             "Connections"})
