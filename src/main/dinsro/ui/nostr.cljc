(ns dinsro.ui.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.ui.nostr.badge-acceptance :as u.n.badge-acceptance]
   [dinsro.ui.nostr.badge-awards :as u.n.badge-awards]
   [dinsro.ui.nostr.badge-definitions :as u.n.badge-definitions]
   [dinsro.ui.nostr.connections :as u.n.connections]
   [dinsro.ui.nostr.event-tags :as u.n.event-tags]
   [dinsro.ui.nostr.events :as u.n.events]
   [dinsro.ui.nostr.filters :as u.n.filters]
   [dinsro.ui.nostr.pubkeys :as u.n.pubkeys]
   [dinsro.ui.nostr.relays :as u.n.relays]
   [dinsro.ui.nostr.requests :as u.n.requests]
   [dinsro.ui.nostr.runs :as u.n.runs]
   [dinsro.ui.nostr.subscription-pubkeys :as u.n.subscription-pubkeys]
   [dinsro.ui.nostr.subscriptions :as u.n.subscriptions]
   [dinsro.ui.nostr.witnesses :as u.n.witnesses]
   [lambdaisland.glogc :as log]))

;; [../ui/nostr/connections.cljs]
;; [../ui/nostr/events.cljs]

(def index-page-key :nostr)

(defrouter Router
  [_this  {:keys [current-state route-factory route-props] :as props}]
  {:router-targets
   [u.n.badge-acceptance/IndexPage
    u.n.badge-awards/IndexPage
    u.n.badge-definitions/IndexPage
    u.n.connections/IndexPage
    u.n.connections/ShowPage
    u.n.event-tags/ShowPage
    u.n.events/IndexPage
    u.n.events/ShowPage
    u.n.filters/ShowPage
    u.n.pubkeys/IndexPage
    u.n.pubkeys/ShowPage
    u.n.relays/NewRelayForm
    u.n.relays/IndexPage
    u.n.relays/ShowPage
    u.n.requests/ShowPage
    u.n.runs/ShowPage
    u.n.subscription-pubkeys/IndexPage
    u.n.subscription-pubkeys/ShowPage
    u.n.subscriptions/IndexPage
    u.n.subscriptions/ShowPage
    u.n.witnesses/IndexPage]}
  (log/debug :Router/starting {:props props})
  (case current-state
    :pending (dom/div :.ui.segment  "Loading...")
    :failed  (dom/div :.ui.segment  "Failed!")
      ;; default will be used when the current state isn't yet set
    (dom/div {}
      (dom/div "No route selected.")
      (when route-factory
        (comp/fragment
         (route-factory route-props))))))

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router] :as props}]
  {:ident          (fn [] [::m.navlinks/id index-page-key])
   :initial-state  (fn [_props]
                     {::m.navlinks/id index-page-key
                      :ui/router      (comp/get-initial-state Router)})
   :query          [::m.navlinks/id
                    {:ui/router (comp/get-query Router)}]
   :route-segment  ["nostr"]}
  (log/info :Page/starting {:props props})
  (if router
    (ui-router router)
    (dom/div :.ui.segment "Failed to load router")))

(m.navlinks/defroute   :nostr
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Nostr"
   ::m.navlinks/model-key     ::m.n.events/id
   ::m.navlinks/parent-key    :root
   ::m.navlinks/required-role :user
   ::m.navlinks/router        :root})
