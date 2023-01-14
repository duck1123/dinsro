(ns dinsro.ui.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.core.addresses :as u.c.addresses]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [dinsro.ui.core.chains :as u.c.chains]
   [dinsro.ui.core.connections :as u.c.connections]
   [dinsro.ui.core.networks :as u.c.networks]
   [dinsro.ui.core.nodes :as u.c.nodes]
   [dinsro.ui.core.peers :as u.c.peers]
   [dinsro.ui.core.tx :as u.c.tx]
   [dinsro.ui.core.wallet-addresses :as u.c.wallet-addresses]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.core.words :as u.c.words]
   [dinsro.ui.nostr.relays :as u.n.relays]))

(defrouter NostrRouter
  [_this {:as props}]
  {:router-targets
   [u.n.relays/RelaysReport]})

(defsc NostrPage
  [_this {:ui/keys [router]}]
  {:query         [{:ui/router (comp/get-query NostrRouter)}]
   :initial-state {:ui/router {}}
   :ident         (fn [] [:component/id ::CorePage])}

  (ui-core-router core-router))

(defsc Page
  [_this {:ui/keys [router]}]
  {:query         [{:ui/router (comp/get-query Router)}]
   :initial-state {:ui/router {}}
   :ident         (fn [] [:component/id ::Page])
   :route-segment ["nostr"]}
  (ui-core-router router))
