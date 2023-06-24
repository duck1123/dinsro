(ns dinsro.ui.settings
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.settings.categories :as u.s.categories]
   [dinsro.ui.settings.core :as u.s.core]
   [dinsro.ui.settings.dashboard :as u.s.dashboard]
   [dinsro.ui.settings.ln :as u.s.ln]
   [dinsro.ui.settings.rate-sources :as u.s.rate-sources]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.s.dashboard/Page
    u.s.core/Page
    u.s.ln/Page
    u.s.rate-sources/Report
    u.s.rate-sources/Show
    u.s.rate-sources/NewForm
    u.s.categories/Report
    u.s.categories/Show
    u.s.categories/NewForm]})

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [nav-menu router vertical-menu]}]
  {:ident         (fn [_] [:page/id ::Page])
   :initial-state (fn [_]
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
        (ui-router router)))))
