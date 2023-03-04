(ns dinsro.ui.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.nostr.badge-awards :as u.n.badge-awards]
   [dinsro.ui.nostr.badge-definitions :as u.n.badge-definitions]
   [dinsro.ui.nostr.events :as u.n.events]
   [dinsro.ui.nostr.pubkeys :as u.n.pubkeys]
   [dinsro.ui.nostr.relays :as u.n.relays]
   [dinsro.ui.nostr.subscription-pubkeys :as u.n.subscription-pubkeys]
   [dinsro.ui.nostr.subscriptions :as u.n.subscriptions]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.badge-awards/Report
    u.n.badge-definitions/Report
    u.n.events/Report
    u.n.events/Show
    u.n.pubkeys/Report
    u.n.pubkeys/Show
    u.n.relays/NewRelayForm
    u.n.relays/Report
    u.n.relays/Show
    u.n.subscription-pubkeys/Report
    u.n.subscription-pubkeys/Show
    u.n.subscriptions/Report
    u.n.subscriptions/Show]})

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router]}]
  {:query         [{:ui/router (comp/get-query Router)}]
   :initial-state {:ui/router {}}
   :ident         (fn [] [:component/id ::Page])
   :route-segment ["nostr"]}
  (ui-router router))
