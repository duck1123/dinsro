(ns dinsro.ui.admin.nostr.requests
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
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.ui.admin.nostr.requests.connections :as u.a.n.rq.connections]
   [dinsro.ui.admin.nostr.requests.filter-items :as u.a.n.rq.filter-items]
   [dinsro.ui.admin.nostr.requests.filters :as u.a.n.rq.filters]
   [dinsro.ui.admin.nostr.requests.runs :as u.a.n.rq.runs]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/requests.cljc]]
;; [[../../../model/nostr/requests.cljc]]

(def index-page-key :admin-nostr-requests)
(def model-key ::m.n.requests/id)
(def parent-router-key :admin-nostr)
(def show-page-key :admin-nostr-requests-show)
(def show-menu-key :admin-nostr-requests)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.rq.connections/SubPage
    u.a.n.rq.filter-items/SubPage
    u.a.n.rq.filters/SubPage
    u.a.n.rq.runs/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-menu-key
  {::m.navbars/parent parent-router-key
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.n.rq.filters/index-page-key
    u.a.n.rq.filter-items/index-page-key
    u.a.n.rq.runs/index-page-key
    u.a.n.rq.connections/index-page-key]})

(defsc Show
  [_this {::m.n.requests/keys [code relay]
          ::j.n.requests/keys [query-string]
          :ui/keys            [nav-menu router]}]
  {:ident         ::m.n.requests/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key                   id
                       ::j.n.requests/query-string ""
                       ::m.n.requests/code         ""
                       ::m.n.requests/relay        (comp/get-initial-state u.links/RelayLinkForm)
                       :ui/nav-menu                (comp/get-initial-state u.menus/NavMenu
                                                     {::m.navbars/id show-menu-key
                                                      :id            id})
                       :ui/router                  (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-menu-key}]
                     :ui/router   [Router {}]})
   :query         [::m.n.requests/code
                   ::m.n.requests/id
                   ::j.n.requests/query-string
                   {::m.n.requests/relay (comp/get-query u.links/RelayLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (ui-segment {}
        (dom/div "Request")
        (dom/div {} (str code))
        (dom/div {} (str "Query String: " query-string))
        (dom/div {} (u.links/ui-relay-link relay)))
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.requests/id #(u.links/ui-admin-request-link %3)}
   ro/columns           [m.n.requests/id
                         m.n.requests/code]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.requests/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.requests/index
   ro/title             "Requests"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["requests"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_props]
                    {::m.navlinks/id     show-page-key
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn [_props]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["request" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (get props model-key)
    (if (seq target)
      (ui-show target)
      (u.debug/load-error props "Admin show request target"))
    (u.debug/load-error props "Admin show request")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Requests"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr
   ::m.navlinks/router        parent-router-key
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/label         "Show Request"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        parent-router-key
   ::m.navlinks/required-role :admin})
