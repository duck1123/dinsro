(ns dinsro.ui.settings
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.mutations]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.settings.categories :as u.s.categories]
   [dinsro.ui.settings.core :as u.s.core]
   [dinsro.ui.settings.ln :as u.s.ln]
   [dinsro.ui.settings.rate-sources :as u.s.rate-sources]))

(defsc Dashboard
  [_this _props]
  {:ident         (fn [] [:component/id ::Dashboard])
   :initial-state {}
   :query         []
   :route-segment ["dashboard"]}
  (ui-grid {}
    (ui-grid-row {:centered true}
      (ui-grid-column {:computer 8 :tablet 8 :mobile 16}
        (ui-container {}
          (dom/div :.ui.segment
            (dom/h1 "Settings"))))
      (ui-grid-column {:computer 8 :tablet 8 :mobile 16}
        (ui-container {}
          (dom/div :.ui.segment
            (dom/h2 "Core Nodes")))))))

(defrouter Router
  [_this _props]
  {:router-targets
   [Dashboard
    u.s.core/Page
    u.s.ln/Page
    u.s.rate-sources/Report
    u.s.rate-sources/Show
    u.s.rate-sources/NewForm
    u.s.categories/Report
    u.s.categories/Show
    u.s.categories/NewForm]})

(defsc SettingsPage
  [_this {:ui/keys [nav-menu router vertical-menu]}]
  {:ident         (fn [_] [:component/id ::Page])
   :initial-state
   (fn [_]
     {:ui/nav-menu      (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :settings})
      :ui/vertical-menu (comp/get-initial-state u.menus/VerticalMenu {::m.navbars/id :settings})
      :ui/router        (comp/get-initial-state Router)})
   :query         [{:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/vertical-menu (comp/get-query u.menus/VerticalMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["settings"]}
  (ui-grid {}
    (ui-grid-row {:only "tablet mobile"}
      (ui-grid-column {:width 16}
        (ui-container {:fluid true}
          (u.menus/ui-nav-menu nav-menu))))
    (ui-grid-row {:centered true}
      (ui-grid-column {:width 4 :only "computer" :floated "left"}
        (u.menus/ui-vertical-menu vertical-menu))
      (ui-grid-column {:mobile 16 :tablet 16 :computer 12}
        ((comp/factory Router) router)))))
