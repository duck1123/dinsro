(ns dinsro.ui.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.nostr.relays :as u.n.relays]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.relays/RelaysReport]})

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router]}]
  {:query         [{:ui/router (comp/get-query Router)}]
   :initial-state {:ui/router {}}
   :ident         (fn [] [:component/id ::Page])
   :route-segment ["nostr"]}
  (ui-router router))
