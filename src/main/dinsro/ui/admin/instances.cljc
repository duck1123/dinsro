(ns dinsro.ui.admin.instances
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.instances :as j.instances]
   [dinsro.model.instances :as m.instances]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.instances :as mu.instances]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/instances.cljc]]
;; [[../../model/instances.cljc]]
;; [[../../ui/admin.cljc]]
;; [[../../ui/instances.cljs]]

(def index-page-id :admin-instances)
(def model-key ::m.instances/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-id :admin-instances-show)

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

(defsc Show
  [_this {::m.instances/keys [last-heartbeat]}]
  {:ident          ::m.instances/id
   :initial-state  {::m.instances/id   nil
                    ::m.instances/last-heartbeat nil}
   ::m.navlinks/id :show-category
   :pre-merge      (u.loader/page-merger model-key {})
   :query          [::m.instances/id
                    ::m.instances/last-heartbeat]}
  (ui-container {}
    (ui-segment {}
      (str last-heartbeat))))

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
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
