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
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.filters.filter-items :as u.n.f.filter-items]
   [lambdaisland.glogc :as log]))

(def model-key ::m.n.filters/id)
(def show-menu-id :nostr-filters)
(def show-page-key :nostr-filters-show)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.f.filter-items/SubPage
    u.n.f.filter-items/NewForm]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu :nostr-filters
  {::m.navbars/parent   :nostr
   ::m.navbars/children
   [u.n.f.filter-items/index-page-key]})

(defsc Show
  [_this {::m.n.filters/keys [id index request]
          :ui/keys           [nav-menu router]
          :as                props}]
  {:ident         ::m.n.filters/id
   :initial-state (fn [props]
                    (let [id (::m.n.filters/id props)]
                      {::m.n.filters/id      nil
                       ::m.n.filters/index   nil
                       ::m.n.filters/request {}
                       :ui/nav-menu          (comp/get-initial-state u.menus/NavMenu
                                               {::m.navbars/id show-menu-id
                                                :id            id})
                       :ui/router            (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-menu-id}]
                     :ui/router   [Router {}]})
   :query         [::m.n.filters/id
                   ::m.n.filters/index
                   {::m.n.filters/request (comp/get-query u.links/RequestLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
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
  [_this {::m.n.filters/keys [id]
          ::m.navlinks/keys  [target]
          :as                props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_props]
                    {model-key           nil
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target {}})
   :query         (fn [_props]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["filter" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {} "Failed to load record")))

(m.navlinks/defroute   :nostr-filters
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Filters"
   ::m.navlinks/model-key     ::m.n.filters/id
   ::m.navlinks/parent-key    :nostr
   ::m.navlinks/router        :nostr
   ::m.navlinks/required-role :user})

(m.navlinks/defroute   :nostr-filters-show
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/label         "Show Filter"
   ::m.navlinks/input-key     ::m.n.filters/id
   ::m.navlinks/model-key     ::m.n.filters/id
   ::m.navlinks/parent-key    :nostr-filters
   ::m.navlinks/router        :nostr
   ::m.navlinks/required-role :user})
