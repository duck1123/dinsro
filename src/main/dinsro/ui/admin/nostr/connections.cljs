(ns dinsro.ui.admin.nostr.connections
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.mutations.nostr.connections :as mu.n.connections]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]))

;; [[../../../joins/nostr/connections.cljc]]
;; [[../../../model/nostr/connections.cljc]]

(def model-key ::m.n.connections/id)

(defsc Show
  [_this {::m.n.connections/keys [id status relay start-time end-time]
          :ui/keys               [nav-menu]}]
  {:ident         ::m.n.connections/id
   :initial-state (fn [props]
                    (let [id (::m.n.connections/id props)]
                      {::m.n.connections/id         nil
                       ::m.n.connections/status     :unknown
                       ::m.n.connections/relay      {}
                       ::m.n.connections/start-time nil
                       ::m.n.connections/end-time   nil
                       :ui/nav-menu                 (comp/get-initial-state
                                                     u.menus/NavMenu
                                                     {::m.navbars/id :admin-nostr-connections
                                                      :id            id})}))
   :query         [::m.n.connections/id
                   ::m.n.connections/status
                   {::m.n.connections/relay (comp/get-query u.links/RelayLinkForm)}
                   ::m.n.connections/start-time
                   ::m.n.connections/end-time
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}]
   :route-segment ["connections" :id]
   :will-enter    (partial u.loader/page-loader ::m.n.connections/id ::Show)}
  (dom/div {}
    (dom/div :.ui.segment
      (dom/div {} (str id))
      (dom/div {} (name status))
      (dom/div {} (u.links/ui-relay-link relay))
      (dom/div {} (str start-time))
      (dom/div {} (str end-time)))
    (u.menus/ui-nav-menu nav-menu)))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.connections/status #(u.links/ui-connection-link %3)
                         ::m.n.connections/relay  #(u.links/ui-relay-link %2)}
   ro/columns           [m.n.connections/status
                         m.n.connections/relay
                         m.n.connections/start-time
                         m.n.connections/end-time
                         j.n.connections/run-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "connections"
   ro/row-actions       [(u.buttons/row-action-button "Disconnect" ::m.n.connections/id mu.n.connections/disconnect!)]
   ro/row-pk            m.n.connections/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.connections/index
   ro/title             "Connections"})
