(ns dinsro.ui.admin.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
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
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../ui/admin/nostr.cljc]]
;; [[../../ui/admin/nostr/filters.cljc]]
;; [[../../ui/admin/nostr/relays.cljc]]
;; [[../../ui/admin/nostr/requests.cljc]]

(def debug-route false)
(def index-page-id :admin-nostr)
(def parent-router-id :admin)
(def required-role :admin)

(defrouter Router
  [_this  {:keys [current-state route-factory route-props router-state] :as props}]
  {:always-render-body? true
   :router-targets
   [u.a.n.dashboard/IndexPage
    u.a.n.badge-acceptances/IndexPage
    u.a.n.badge-acceptances/ShowPage
    u.a.n.badge-awards/IndexPage
    u.a.n.badge-awards/ShowPage
    u.a.n.badge-definitions/IndexPage
    u.a.n.badge-definitions/ShowPage
    u.a.n.connections/IndexPage
    u.a.n.connections/ShowPage
    u.a.n.events/IndexPage
    u.a.n.events/ShowPage
    u.a.n.filter-items/IndexPage
    u.a.n.filter-items/ShowPage
    u.a.n.filters/IndexPage
    u.a.n.filters/ShowPage
    u.a.n.pubkeys/IndexPage
    u.a.n.pubkeys/ShowPage
    u.a.n.relays/IndexPage
    u.a.n.relays/ShowPage
    u.a.n.requests/IndexPage
    u.a.n.requests/ShowPage
    u.a.n.runs/IndexPage
    u.a.n.runs/ShowPage
    u.a.n.witnesses/IndexPage
    u.a.n.witnesses/ShowPage]}
  (log/debug :Router/starting {:props props})
  (dom/div :.admin-nostr-router-outer {}
    (case current-state
      :pending
      (dom/div {} "Loading...")

      :failed
      (dom/div {}
        (ui-segment {} "Failed!")
        (u.debug/log-props props))

      :routed  (route-factory route-props)

      ;; default will be used when the current state isn't yet set
      (dom/div :.admin-nostr-router
        (dom/div "No route selected.")
        (u.debug/log-props {:current-state current-state :router-state router-state})))
    (when debug-route
      (ui-segment {}
        (dom/h3 {} "Admin Nostr Router")
        (u.debug/log-props props)))))

(def ui-router (comp/factory Router))

(m.navbars/defmenu index-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.n.dashboard/index-page-id
    u.a.n.relays/index-page-id
    u.a.n.pubkeys/index-page-id
    u.a.n.events/index-page-id
    u.a.n.filters/index-page-id
    u.a.n.badge-acceptances/index-page-id
    u.a.n.badge-awards/index-page-id
    u.a.n.badge-definitions/index-page-id
    u.a.n.requests/index-page-id
    u.a.n.connections/index-page-id
    u.a.n.filter-items/index-page-id
    u.a.n.runs/index-page-id
    u.a.n.witnesses/index-page-id]})

(defsc IndexPage
  [_this {:ui/keys [nav-menu router vertical-menu] :as props}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state (fn [props]
                    (log/debug :IndexPage/initial-state {:props props})
                    (let [state {::m.navlinks/id   :admin-nostr
                                 :ui/nav-menu      (comp/get-initial-state u.menus/NavMenu
                                                     {::m.navbars/id index-page-id})
                                 :ui/vertical-menu (comp/get-initial-state u.menus/VerticalMenu
                                                     {::m.navbars/id index-page-id})
                                 :ui/router        (comp/get-initial-state Router)}]
                      (log/debug :IndexPage/initial-state-generated {:props props :state state})
                      state))
   :query         [::m.navlinks/id
                   {:ui/nav-menu (comp/get-query u.menus/VerticalMenu)}
                   {:ui/router (comp/get-query Router)}
                   {:ui/vertical-menu (comp/get-query u.menus/VerticalMenu)}]
   :route-segment ["nostr"]}
  (log/debug :IndexPage/starting {:props props})
  (ui-grid {:centered true}
    (ui-grid-row {:only "tablet mobile"}
      (ui-grid-column {:width 16}
        (if nav-menu
          (u.menus/ui-nav-menu nav-menu)
          (u.debug/load-error props "admin nostr nav menu"))))
    (ui-grid-row {}
      (ui-grid-column {:only "computer" :width 3}
        (if vertical-menu
          (u.menus/ui-vertical-menu vertical-menu)
          (u.debug/load-error props "admin nostr nav menu")))
      (ui-grid-column {:tablet 16 :computer 13 :stretched true}
        (ui-grid {:centered true}
          (ui-grid-row {}
            (ui-grid-column {:computer 16 :tablet 16 :mobile 16}
              (if router
                (ui-router router)
                (u.debug/load-error props "admin nostr router")))))))))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Nostr"
   o.navlinks/navigate-key  u.a.n.dashboard/index-page-id
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
