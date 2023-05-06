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
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-list :refer [ui-list-list]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.requests :as mu.n.requests]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.nostr.requests :as o.n.requests]
   [dinsro.ui.admin.nostr.filters :as u.a.n.filters]
   [dinsro.ui.admin.nostr.requests.connections :as u.a.n.rq.connections]
   [dinsro.ui.admin.nostr.requests.filter-items :as u.a.n.rq.filter-items]
   [dinsro.ui.admin.nostr.requests.filters :as u.a.n.rq.filters]
   [dinsro.ui.admin.nostr.requests.runs :as u.a.n.rq.runs]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]))

;; [[../../../joins/nostr/requests.cljc]]
;; [[../../../model/nostr/requests.cljc]]
;; [[../../../mutations/nostr/requests.cljc]]
;; [[../../../ui/admin/nostr.cljc]]
;; [[../../../ui/forms/nostr/requests.cljc]]
;; [[../../../ui/nostr/requests.cljc]]

(def index-page-id :admin-nostr-requests)
(def model-key o.n.requests/id)
(def parent-router-id :admin-nostr)
(def required-role :admin)
(def show-page-id :admin-nostr-requests-show)
(def debug-props false)

(def delete-action
  (u.links/row-action-button "Delete" model-key mu.n.requests/stop!))

(defsc BodyItem
  [this {::j.n.requests/keys [filters]
         ::m.n.requests/keys [code]
         :as                 props}]
  {:ident         ::m.n.requests/id
   :query         [::m.n.requests/id
                   ::m.n.requests/code
                   ::j.n.requests/run-count
                   {::j.n.requests/filters (comp/get-query u.a.n.filters/FilterItem {})}
                   ::j.n.requests/filter-count
                   ::j.n.requests/query-string]
   :initial-state {::m.n.requests/id           nil
                   ::m.n.requests/code         ""
                   ::j.n.requests/run-count    0
                   ::j.n.requests/filters      []
                   ::j.n.requests/filter-count 0
                   ::j.n.requests/query-string ""}}
  (ui-list-item {}
    (ui-segment {}
      (dom/div {}
        (dom/h2 {} (str code))
        (dom/div {}
          (u.buttons/action-button `mu.n.requests/edit "Edit" model-key this)
          (u.buttons/delete-button `mu.n.requests/delete! model-key this)))
      (ui-list-list {}
        (map u.a.n.filters/ui-filter-item filters))
      (when debug-props (u.debug/log-props props)))))

(def ui-body-item (comp/factory BodyItem {:keyfn ::m.n.requests/id}))

(report/defsc-report Report
  [_this props]
  {ro/BodyItem          BodyItem
   ro/column-formatters {::m.n.requests/id    #(u.links/ui-admin-request-link %3)
                         ::m.n.requests/code  #(u.links/ui-admin-request-link %3)}
   ro/columns           [m.n.requests/id
                         m.n.requests/code
                         j.n.requests/run-count
                         j.n.requests/filter-count
                         j.n.requests/query-string]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.n.requests/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.requests/admin-index
   ro/title             "Requests"}
  (let [{:ui/keys [current-rows]} props]
    (ui-segment {}
      (dom/h1 {} "Requests")
      (u.buttons/refresh-button this)
      (ui-list-list {}
        (map ui-body-item current-rows)))))

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.rq.connections/SubPage
    u.a.n.rq.filter-items/SubPage
    u.a.n.rq.filters/SubPage
    u.a.n.rq.runs/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.n.rq.filters/index-page-id
    u.a.n.rq.filter-items/index-page-id
    u.a.n.rq.runs/index-page-id
    u.a.n.rq.connections/index-page-id]})

(defsc Show
  [_this {::m.n.requests/keys [code id relay]
          ::j.n.requests/keys [query-string]
          :ui/keys            [admin-nav-menu admin-router]
          :as                 props}]
  {:ident         ::m.n.requests/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key                   id
                       ::j.n.requests/query-string ""
                       ::m.n.requests/code         ""
                       ::m.n.requests/relay        (comp/get-initial-state u.links/AdminRelayLinkForm)
                       :ui/admin-nav-menu          (comp/get-initial-state u.menus/NavMenu
                                                     {::m.navbars/id show-page-id
                                                      :id            id})
                       :ui/admin-router            (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/admin-nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                     :ui/admin-router   [Router {}]})
   :query         (fn []
                    [::m.n.requests/code
                     ::m.n.requests/id
                     ::j.n.requests/query-string
                     {::m.n.requests/relay (comp/get-query u.links/RelayLinkForm)}
                     {:ui/admin-nav-menu (comp/get-query u.menus/NavMenu)}
                     {:ui/admin-router (comp/get-query Router)}])}
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (ui-segment {}
          (dom/div "Request")
          (dom/div {} (str code))
          (dom/div {} (str "Query String: " query-string))
          (dom/div {} (u.links/ui-relay-link relay)))
        (u.menus/ui-nav-menu admin-nav-menu)
        (ui-router admin-router)))
    (u.debug/load-error props "admin show request")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["requests"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key           (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["request" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Requests"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Request"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
