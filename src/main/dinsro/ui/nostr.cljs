(ns dinsro.ui.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.nostr.events :as u.n.events]
   [dinsro.ui.nostr.pubkeys :as u.n.pubkeys]
   [dinsro.ui.nostr.relays :as u.n.relays]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.relays/Report
    u.n.pubkeys/Report
    u.n.pubkeys/Show
    u.n.events/Report]})

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router]}]
  {:query         [{:ui/router (comp/get-query Router)}]
   :initial-state {:ui/router {}}
   :ident         (fn [] [:component/id ::Page])
   :route-segment ["nostr"]}
  (ui-router router))
