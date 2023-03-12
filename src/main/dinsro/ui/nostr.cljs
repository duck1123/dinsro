(ns dinsro.ui.nostr
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.nostr.badge-acceptance :as u.n.badge-acceptance]
   [dinsro.ui.nostr.badge-awards :as u.n.badge-awards]
   [dinsro.ui.nostr.badge-definitions :as u.n.badge-definitions]
   [dinsro.ui.nostr.events :as u.n.events]
   [dinsro.ui.nostr.filters :as u.n.filters]
   [dinsro.ui.nostr.pubkeys :as u.n.pubkeys]
   [dinsro.ui.nostr.relays :as u.n.relays]
   [dinsro.ui.nostr.requests :as u.n.requests]
   [dinsro.ui.nostr.subscription-pubkeys :as u.n.subscription-pubkeys]
   [dinsro.ui.nostr.subscriptions :as u.n.subscriptions]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.badge-acceptance/Report
    u.n.badge-awards/Report
    u.n.badge-definitions/Report
    u.n.events/Report
    u.n.events/Show
    u.n.filters/Show
    u.n.pubkeys/Report
    u.n.pubkeys/Show
    u.n.relays/NewRelayForm
    u.n.relays/Report
    u.n.relays/Show
    u.n.requests/Show
    u.n.subscription-pubkeys/Report
    u.n.subscription-pubkeys/Show
    u.n.subscriptions/Report
    u.n.subscriptions/Show]})

(defsc Page
  [_this {:ui/keys [router]}]
  {:css [[:.router-wrapper {:border "1px solid purple"}]]
   :ident         (fn [] [:component/id ::Page])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["nostr"]}
  (let [{:keys [router-wrapper]} (css/get-classnames Page)]
    (dom/div {:classes [:.nostr-page router-wrapper]}
      ((comp/factory Router) router))))
