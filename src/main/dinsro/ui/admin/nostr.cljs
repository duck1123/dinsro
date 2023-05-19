(ns dinsro.ui.admin.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.ui.admin.nostr.badge-acceptances :as u.a.n.badge-acceptances]
   [dinsro.ui.admin.nostr.badge-awards :as u.a.n.badge-awards]
   [dinsro.ui.admin.nostr.badge-definitions :as u.a.n.badge-definitions]
   [dinsro.ui.admin.nostr.connections :as u.a.n.connections]
   [dinsro.ui.admin.nostr.dashboard :as u.a.n.dashboard]
   [dinsro.ui.admin.nostr.events :as u.a.n.events]
   [dinsro.ui.admin.nostr.filter-items :as u.a.n.filter-items]
   [dinsro.ui.admin.nostr.filters :as u.a.n.filters]
   [dinsro.ui.admin.nostr.pubkeys :as u.a.n.pubkeys]
   [dinsro.ui.admin.nostr.relays :as u.a.n.relays]
   [dinsro.ui.admin.nostr.requests :as u.a.n.requests]
   [dinsro.ui.admin.nostr.runs :as u.a.n.runs]
   [dinsro.ui.admin.nostr.witnesses :as u.a.n.witnesses]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.dashboard/Dashboard
    u.a.n.badge-acceptances/Report
    u.a.n.badge-awards/Report
    u.a.n.badge-definitions/Report
    u.a.n.connections/Report
    u.a.n.connections/Show
    u.a.n.events/Report
    u.a.n.filter-items/Report
    u.a.n.filters/Report
    u.a.n.pubkeys/Report
    u.a.n.relays/Report
    u.a.n.relays/Show
    u.a.n.requests/Report
    u.a.n.runs/Report
    u.a.n.witnesses/Report]})

(defsc Page
  [_this {:ui/keys [nav-menu router vertical-menu]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state
   (fn [props]
     (log/trace :Page/initial-state {:props props})
     {:ui/nav-menu      (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :admin-nostr})
      :ui/vertical-menu (comp/get-initial-state u.menus/VerticalMenu {::m.navbars/id :admin-nostr})
      :ui/router        (comp/get-initial-state Router)})
   :query         [{:ui/nav-menu (comp/get-query u.menus/VerticalMenu)}
                   {:ui/router (comp/get-query Router)}
                   {:ui/vertical-menu (comp/get-query u.menus/VerticalMenu)}]
   :route-segment ["nostr"]}
  (ui-grid {:centered true}
    (ui-grid-row {:only "tablet mobile"}
      (ui-grid-column {:width 16}
        (u.menus/ui-nav-menu nav-menu)))
    (ui-grid-row {}
      (ui-grid-column {:only "computer" :width 3}
        (u.menus/ui-vertical-menu vertical-menu))
      (ui-grid-column {:tablet 16 :computer 13 :stretched true}
        (ui-grid {:centered true}
          (ui-grid-row {}
            (ui-grid-column {:computer 16 :tablet 16 :mobile 16}
              ((comp/factory Router) router))))))))
