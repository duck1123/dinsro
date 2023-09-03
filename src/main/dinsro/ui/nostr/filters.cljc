(ns dinsro.ui.nostr.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.options.navbars :as o.navbars]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.nostr.filters :as o.n.filters]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.filters.filter-items :as u.n.f.filter-items]))

;; [[../../ui/admin/nostr/filters.cljc]]

(def model-key o.n.filters/id)
(def parent-router-id :nostr)
(def required-role :user)
(def show-page-id :nostr-filters-show)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.f.filter-items/SubPage
    u.n.f.filter-items/NewForm]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent   parent-router-id
   ::m.navbars/children
   [u.n.f.filter-items/index-page-id]})

(defsc Show
  [_this {::m.n.filters/keys [id index request]
          :ui/keys           [nav-menu router]
          :as                props}]
  {:ident         ::m.n.filters/id
   :initial-state (fn [props]
                    (let [id (o.n.filters/id props)]
                      {o.n.filters/id      nil
                       o.n.filters/index   nil
                       o.n.filters/request {}
                       :ui/nav-menu          (comp/get-initial-state u.menus/NavMenu
                                               {o.navbars/id show-page-id
                                                :id            id})
                       :ui/router            (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {o.navbars/id show-page-id}]
                     :ui/router   [Router {}]})
   :query         (fn []
                    [model-key
                     o.n.filters/index
                     {o.n.filters/request (comp/get-query u.links/RequestLinkForm)}
                     {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                     {:ui/router (comp/get-query Router)}])}
  (if id
    (ui-segment {}
      (dom/div {} (str id))
      (dom/div {} (str index))
      (dom/div {}
        (if request
          (u.links/ui-request-link request)
          (u.debug/load-error props "show nostr filter request")))
      (if nav-menu
        (u.menus/ui-nav-menu nav-menu)
        (u.debug/load-error props "show nostr filter menu"))
      (if router
        (ui-router router)
        (u.debug/load-error props "show nostr filter router")))
    (u.debug/load-error props "show nostr filter record")))

(def ui-show (comp/factory Show))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state (fn [_props]
                    {model-key           nil
                     ::m.navlinks/id     show-page-id
                     ::m.navlinks/target {}})
   :query         (fn [_props]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["filter" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Filter"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
