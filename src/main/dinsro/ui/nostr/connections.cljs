(ns dinsro.ui.nostr.connections
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.connections.runs :as u.n.c.runs]))

;; [[../../ui/nostr/connections/runs.cljs]]

(defsc ConnectionDisplay
  [_this {::m.n.connections/keys [relay] :as props}]
  {:ident         ::m.n.connections/id
   :initial-state {::m.n.connections/id     nil
                   ::m.n.connections/status :initial
                   ::m.n.connections/relay  {}}
   :query         [::m.n.connections/id
                   ::m.n.connections/status
                   {::m.n.connections/relay (comp/get-query u.links/RelayLinkForm)}]}
  (u.links/ui-relay-link props)
  (u.links/ui-relay-link relay))

(def ui-connection-display (comp/factory ConnectionDisplay {:keyfn ::m.n.connections/id}))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.c.runs/SubPage]})

(defsc Show
  [_this {::m.n.connections/keys [id status relay start-time end-time]
          :ui/keys               [nav-menu router]}]
  {:ident         ::m.n.connections/id
   :initial-state
   (fn [props]
     (let [id (::m.n.connections/id props)]
       {::m.n.connections/id         nil
        ::m.n.connections/status     :unknown
        ::m.n.connections/relay      {}
        ::m.n.connections/start-time nil
        ::m.n.connections/end-time   nil
        :ui/nav-menu
        (comp/get-initial-state       u.menus/NavMenu
                                      {::m.navbars/id :nostr-connections :id id})
        :ui/router                   (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger
                   ::m.n.connections/id
                   {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id :nostr-connections}]
                    :ui/router   [Router {}]})
   :query         [::m.n.connections/id
                   ::m.n.connections/status
                   {::m.n.connections/relay (comp/get-query u.links/RelayLinkForm)}
                   ::m.n.connections/start-time
                   ::m.n.connections/end-time
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["connections" :id]
   :will-enter    (partial u.loader/page-loader ::m.n.connections/id ::Show)}
  (dom/div {}
    (dom/div :.ui.segment
      (dom/div {} (str id))
      (dom/div {} (name status))
      (dom/div {} (u.links/ui-relay-link relay))
      (dom/div {} (str start-time))
      (dom/div {} (str end-time)))
    (u.menus/ui-nav-menu nav-menu)
    ((comp/factory Router) router)))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.connections/relay     #(u.links/ui-relay-link %2)
                         ::m.n.connections/status    #(u.links/ui-connection-link %3)
                         ::j.n.connections/run-count #(u.links/ui-connection-run-count-link %3)}
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
   ro/row-pk            m.n.connections/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.connections/index
   ro/title             "Connections"})
