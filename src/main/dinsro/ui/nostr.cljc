(ns dinsro.ui.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.nostr.relays :as u.f.n.relays]
   [dinsro.ui.nostr.badge-acceptance :as u.n.badge-acceptance]
   [dinsro.ui.nostr.badge-awards :as u.n.badge-awards]
   [dinsro.ui.nostr.badge-definitions :as u.n.badge-definitions]
   [dinsro.ui.nostr.event-tags :as u.n.event-tags]
   [dinsro.ui.nostr.events :as u.n.events]
   [dinsro.ui.nostr.filters :as u.n.filters]
   [dinsro.ui.nostr.pubkeys :as u.n.pubkeys]
   [dinsro.ui.nostr.relays :as u.n.relays]
   [dinsro.ui.nostr.requests :as u.n.requests]
   [dinsro.ui.nostr.witnesses :as u.n.witnesses]
   [lambdaisland.glogc :as log]))

;; [[../ui/nostr/connections.cljs]]
;; [[../ui/nostr/events.cljs]]

(def index-page-id :nostr)
(def parent-router-id :root)
(def required-role :guest)

(defrouter Router
  [_this  {:keys [current-state route-factory route-props] :as props}]
  {:router-targets
   [u.n.badge-acceptance/IndexPage
    u.n.badge-awards/IndexPage
    u.n.badge-definitions/IndexPage
    u.n.event-tags/ShowPage
    u.n.events/IndexPage
    u.n.events/ShowPage
    u.n.filters/ShowPage
    u.n.pubkeys/IndexPage
    u.n.pubkeys/ShowPage
    u.f.n.relays/NewRelayForm
    u.n.relays/IndexPage
    u.n.relays/ShowPage
    u.n.requests/ShowPage
    u.n.witnesses/IndexPage]}
  (log/debug :Router/starting {:props props})
  (case current-state
    :pending
    (ui-segment {} "Loading...")

    :failed
    (ui-segment {} "Failed!")

    ;; default will be used when the current state isn't yet set
    (dom/div {}
      (dom/div "No route selected.")
      (when route-factory
        (comp/fragment
         (route-factory route-props))))))

(def ui-router (comp/factory Router))

(defsc IndexPage
  [_this {:ui/keys [router] :as props}]
  {:ident          (fn [] [::m.navlinks/id index-page-id])
   :initial-state  (fn [_props]
                     {::m.navlinks/id index-page-id
                      :ui/router      (comp/get-initial-state Router)})
   :query          [::m.navlinks/id
                    {:ui/router (comp/get-query Router)}]
   :route-segment  ["nostr"]}
  (log/info :IndexPage/starting {:props props})
  (if router
    (ui-router router)
    (u.debug/load-error "Nostr router")))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::Page
   o.navlinks/label         "Nostr"
   o.navlinks/parent-key    parent-router-id
   o.navlinks/required-role required-role
   o.navlinks/router        parent-router-id})
