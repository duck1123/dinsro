(ns dinsro.ui.admin.instances
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.instances :as j.instances]
   [dinsro.model.instances :as m.instances]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.instances :as mu.instances]
   [dinsro.options.instances :as o.instances]
   [dinsro.options.navbars :as o.navbars]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.admin.instances.connections :as u.a.i.connections]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../joins/instances.cljc]]
;; [[../../model/instances.cljc]]
;; [[../../ui/admin.cljc]]
;; [[../../ui/instances.cljs]]

(def index-page-id :admin-instances)
(def model-key o.instances/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-id :admin-instances-show)

(def debug-props? false)

(def beat-action
  (u.buttons/row-action-button "Beat" model-key mu.instances/beat!))

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.instances/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.instances/id             #(u.links/ui-admin-instance-link %3)
                         ::m.instances/created-time   u.controls/date-formatter
                         ::m.instances/last-heartbeat u.controls/date-formatter}
   ro/columns           [m.instances/id
                         m.instances/created-time
                         m.instances/last-heartbeat
                         j.instances/alive?
                         j.instances/connection-count]
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [beat-action delete-action]
   ro/row-pk            m.instances/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.instances/admin-index
   ro/title             "Instances"})

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.i.connections/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.i.connections/index-page-id]})

(defsc Show
  [_this {last-heartbeat   o.instances/last-heartbeat
          connection-count ::j.instances/connection-count
          admin-nav-menu   :ui/admin-nav-menu
          admin-router     :ui/admin-router
          :as              props}]
  {:ident         ::m.instances/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key                      id
                       ::j.instances/connection-count 0
                       o.instances/last-heartbeat     nil
                       :ui/admin-nav-menu             (comp/get-initial-state u.menus/NavMenu
                                                        {::m.navbars/id show-page-id
                                                         :id            id})
                       :ui/admin-router               (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/admin-nav-menu [u.menus/NavMenu {o.navbars/id show-page-id}]
                     :ui/admin-router   [Router {}]})
   :query         (fn []
                    [::j.instances/connection-count
                     o.instances/id
                     o.instances/last-heartbeat
                     {:ui/admin-nav-menu (comp/get-query u.menus/NavMenu)}
                     {:ui/admin-router (comp/get-query Router)}])}
  (ui-container {}
    (ui-segment {}
      (str last-heartbeat)
      (dom/div {}
        (str "Count: " connection-count))
      (when debug-props?
        (u.debug/ui-props-logger props)))
    (if admin-nav-menu
      (u.menus/ui-nav-menu admin-nav-menu)
      (u.debug/load-error props "admin networks show menu"))
    (if admin-router
      (ui-router admin-router)
      (u.debug/load-error props "admin networks show router"))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["instances"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/info :Page/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (u.debug/load-error props "admin index instances page"))))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["instance" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/description   "Admin page of all instances"
   o.navlinks/label         "Instances"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin page for instances"
   o.navlinks/label         "Show Instance"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/navigate-key  u.a.i.connections/index-page-id
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
