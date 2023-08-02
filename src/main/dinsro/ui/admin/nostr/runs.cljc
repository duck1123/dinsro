(ns dinsro.ui.admin.nostr.runs
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.runs :as j.n.runs]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations.nostr.runs :as mu.n.runs]
   [dinsro.ui.admin.nostr.relays.witnesses :as u.a.n.r.witnesses]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/runs.cljc]]
;; [[../../../model/nostr/runs.cljc]]

(def index-page-key :admin-nostr-runs)
(def model-key ::m.n.runs/id)
(def show-menu-id :admin-nostr-runs)
(def show-page-key :admin-nostr-runs-show)

(def stop-action
  (u.buttons/row-action-button "Stop" model-key mu.n.runs/stop!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.runs/connection #(u.links/ui-admin-connection-link %2)
                         ::m.n.runs/request    #(u.links/ui-admin-request-link %2)
                         ::j.n.runs/relay      #(u.links/ui-admin-relay-link %2)}
   ro/columns           [m.n.runs/status
                         m.n.runs/request
                         m.n.runs/connection
                         m.n.runs/start-time
                         m.n.runs/finish-time
                         m.n.runs/end-time
                         j.n.runs/relay]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "runs"
   ro/row-actions       [stop-action]
   ro/row-pk            m.n.runs/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.runs/index
   ro/title             "Runs"})

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.r.witnesses/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-menu-id
  {::m.navbars/parent :admin-nostr
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.n.r.witnesses/index-page-key]})

(defsc Show
  [_this {::m.n.runs/keys [id]
          :ui/keys        [nav-menu router]
          :as             props}]
  {:ident         ::m.n.runs/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key    id
                       :ui/router   (comp/get-initial-state Router)
                       :ui/nav-menu (comp/get-initial-state u.menus/NavMenu
                                      {::m.navbars/id show-menu-id
                                       :id            id})}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-menu-id}]
                     :ui/router   [Router {}]})
   :query         (fn []
                    [::m.n.runs/id
                     {:ui/nav-menu (comp/get-query u.menus/NavMenu {})}
                     {:ui/router (comp/get-query Router {})}])}
  (log/info :Show/starting {:props props})
  (if (model-key props)
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (ui-segment {}
          (dom/div "Run")
          (dom/div {} (str id)))
        (u.menus/ui-nav-menu nav-menu)
        (ui-router router)))
    (u.debug/load-error props "admin show runs")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["runs"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [props]
                    {model-key           (model-key props)
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["run" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (model-key props)
    (if target
      (ui-show target)
      (u.debug/load-error props "admin show runs"))
    (u.debug/load-error props "admin show runs page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Runs"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/label         "Show Runs"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})
