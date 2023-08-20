(ns dinsro.ui.admin.nostr.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.filters :as j.n.filters]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.mutations.nostr.filters :as mu.n.filters]
   [dinsro.ui.admin.nostr.filters.filter-items :as u.a.n.f.filter-items]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/filters.cljc]]
;; [[../../../model/nostr/filters.cljc]]
;; [[../../../ui/admin/nostr/requests/filters.cljc]]
;; [[../../../ui/nostr/filters.cljc]]

(def index-page-key :admin-nostr-filters)
(def model-key ::m.n.filters/id)
(def show-menu-id :admin-nostr-filters)
(def show-page-key :admin-nostr-filters-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.filters/delete!))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.f.filter-items/SubPage
    u.a.n.f.filter-items/NewForm]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-menu-id
  {::m.navbars/parent :admin-nostr
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.n.f.filter-items/index-page-key]})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filters/request #(u.links/ui-admin-request-link %2)
                         ::m.n.filters/index   #(u.links/ui-admin-filter-link %3)}
   ro/columns           [m.n.filters/index
                         m.n.filters/request
                         m.n.filters/since
                         m.n.filters/until
                         j.n.filters/item-count
                         j.n.filters/query-string]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.n.filters/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filters/admin-index
   ro/title             "Filters"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::j.n.filters/keys [query-string]
          ::m.n.filters/keys [id index request]
          :ui/keys           [admin-nav-menu admin-router]
          :as                props}]
  {:ident         ::m.n.filters/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key                  id
                       ::j.n.filters/query-string ""
                       ::m.n.filters/index        nil
                       ::m.n.filters/request      (comp/get-initial-state u.links/AdminRequestLinkForm)
                       :ui/admin-nav-menu         (comp/get-initial-state u.menus/NavMenu
                                                    {::m.navbars/id show-menu-id
                                                     :id            id})
                       :ui/admin-router           (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/admin-nav-menu [u.menus/NavMenu {::m.navbars/id show-menu-id}]
                     :ui/admin-router   [Router {}]})
   :query         (fn []
                    [::j.n.filters/query-string
                     ::m.n.filters/id
                     ::m.n.filters/index
                     {::m.n.filters/request (comp/get-query u.links/AdminRequestLinkForm)}
                     {:ui/admin-nav-menu (comp/get-query u.menus/NavMenu)}
                     {:ui/admin-router (comp/get-query Router)}])}
  (log/info :Show/starting {:props props})
  (if id
    (ui-segment {}
      (dom/div {} (str id))
      (dom/div {} (str index))
      (dom/div {} (str query-string))
      (dom/div {}
        (if request
          (u.links/ui-request-link request)
          (u.debug/load-error props "admin show nostr filter request")))
      (if admin-nav-menu
        (u.menus/ui-nav-menu admin-nav-menu)
        (u.debug/load-error props "admin show nostr filter menu"))
      (if admin-router
        (ui-router admin-router)
        (u.debug/load-error props "admin show nostr filter router")))
    (u.debug/load-error props "admin show nostr filter record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["filters"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/info :IndexPage/starting {:props props})
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
   :route-segment ["filter" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (model-key props)
    (if target
      (ui-show target)
      (u.debug/load-error props "admin show nostr filter target"))
    (u.debug/load-error props "admin show nostr filter page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Filters"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Filter"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})
