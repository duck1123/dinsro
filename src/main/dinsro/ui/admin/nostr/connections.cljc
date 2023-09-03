(ns dinsro.ui.admin.nostr.connections
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.mutations.nostr.connections :as mu.n.connections]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.admin.nostr.connections.runs :as u.a.n.c.runs]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/connections.cljc]]
;; [[../../../model/nostr/connections.cljc]]
;; [[../../../mutations/nostr/connections.cljc]]
;; [[../../../ui/admin/nostr/connections/runs.cljc]]
;; [[../../../ui/nostr/connections.cljs]]

(def index-page-id :admin-nostr-connections)
(def model-key ::m.n.connections/id)
(def parent-router-id :admin-nostr)
(def required-role :admin)
(def show-page-id :admin-nostr-connections-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.connections/delete!))

(def disconnect-action
  (u.buttons/row-action-button "Disconnect" model-key mu.n.connections/disconnect!))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.c.runs/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent   parent-router-id
   ::m.navbars/router   ::Router
   ::m.navbars/children [u.a.n.c.runs/index-page-id]})

(defsc Show
  [_this {::m.n.connections/keys [id status relay start-time end-time instance]
          :ui/keys               [nav-menu router]
          :as                    props}]
  {:ident         ::m.n.connections/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {::m.n.connections/end-time   nil
                       ::m.n.connections/id         id
                       ::m.n.connections/instance   {}
                       ::m.n.connections/relay      {}
                       ::m.n.connections/status     :unknown
                       ::m.n.connections/start-time nil
                       :ui/nav-menu                 (comp/get-initial-state u.menus/NavMenu
                                                      {::m.navbars/id show-page-id
                                                       :id            id})
                       :ui/router                   (comp/get-initial-state Router {})}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                     :ui/router   [Router {}]})
   :query         [::m.n.connections/end-time
                   ::m.n.connections/id
                   {::m.n.connections/instance (comp/get-query u.links/AdminInstanceLinkForm)}
                   {::m.n.connections/relay (comp/get-query u.links/AdminRelayLinkForm)}
                   ::m.n.connections/start-time
                   ::m.n.connections/status
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/div {} (str id))
        (dom/div {} (name status))
        (dom/div {} (u.links/ui-relay-link relay))
        (dom/div {} (str start-time))
        (dom/div {} (str end-time))
        (dom/div {} (u.links/ui-admin-instance-link instance)))
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))
    (u.debug/load-error props "admin show connection record")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.connections/status   #(u.links/ui-admin-connection-link %3)
                         ::m.n.connections/relay    #(when %2 (u.links/ui-admin-relay-link %2))
                         ::m.n.connections/instance #(when %2 (u.links/ui-admin-instance-link %2))}
   ro/columns           [m.n.connections/status
                         m.n.connections/relay
                         m.n.connections/instance
                         m.n.connections/start-time
                         m.n.connections/end-time
                         j.n.connections/run-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [disconnect-action
                         delete-action]
   ro/row-pk            m.n.connections/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.connections/admin-index
   ro/title             "Connections"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["connections"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id      show-page-id
                     o.navlinks/target  (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["connections" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Connections"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Connection"
   o.navlinks/model-key     model-key
   o.navlinks/navigate-key  u.a.n.c.runs/index-page-id
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
