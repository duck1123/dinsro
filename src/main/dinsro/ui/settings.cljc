(ns dinsro.ui.settings
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.settings.categories :as u.s.categories]
   [dinsro.ui.settings.core :as u.s.core]
   [dinsro.ui.settings.dashboard :as u.s.dashboard]
   [dinsro.ui.settings.ln :as u.s.ln]
   [dinsro.ui.settings.rate-sources :as u.s.rate-sources]
   [lambdaisland.glogc :as log]))

(def index-page-id :settings)
(def parent-router-id :root)
(def required-role :user)

(defrouter Router
  [_this {:keys [current-state route-factory route-props] :as  props}]
  {:router-targets
   [u.s.dashboard/Page
    u.s.core/Page
    u.s.ln/Page
    u.s.rate-sources/IndexPage
    u.s.rate-sources/ShowPage
    u.s.rate-sources/NewForm
    u.s.categories/IndexPage
    u.s.categories/ShowPage
    u.s.categories/NewForm]}
  (log/info :Router/starting {:props props})
  (case current-state
    :pending
    (ui-segment {}
      "Loading...")

    :failed
    (ui-segment {}
      "Failed!")

    ;; default will be used when the current state isn't yet set
    (dom/div {}
      (dom/div "No route selected.")
      (when route-factory
        (comp/fragment
         (route-factory route-props))))))

(def ui-router (comp/factory Router))

(m.navbars/defmenu index-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.s.dashboard/index-page-id
    u.s.core/index-page-id
    u.s.ln/index-page-id
    u.s.rate-sources/index-page-id
    u.s.categories/index-page-id]})

(defsc IndexPage
  [_this {:ui/keys [nav-menu router vertical-menu]
          :as props}]
  {:ident          (fn [_] [::m.navlinks/id index-page-id])
   :initial-state  (fn [_]
                     {::m.navlinks/id   index-page-id
                      :ui/nav-menu      (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :settings})
                      :ui/vertical-menu (comp/get-initial-state u.menus/VerticalMenu {::m.navbars/id :settings})
                      :ui/router        (comp/get-initial-state Router)})
   :query          (fn [_]
                     [{:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                      {:ui/vertical-menu (comp/get-query u.menus/VerticalMenu)}
                      {:ui/router (comp/get-query Router)}])
   :route-segment  ["settings"]}
  (ui-grid {}
    (ui-grid-row {:only "tablet mobile"}
      (ui-grid-column {:width 16}
        (ui-container {:fluid true}
          (if nav-menu
            (u.menus/ui-nav-menu nav-menu)
            (dom/div :.ui-segment "Failed to load menu")))))
    (ui-grid-row {:centered true}
      (ui-grid-column {:width 4 :only "computer" :floated "left"}
        (u.menus/ui-vertical-menu vertical-menu))
      (ui-grid-column {:mobile 16 :tablet 16 :computer 12}
        (if router
          (ui-router router)
          (u.debug/load-error props "settings router"))))))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Settings"
   ::m.navlinks/navigate-key  u.s.dashboard/index-page-id
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
