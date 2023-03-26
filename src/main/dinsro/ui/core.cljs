(ns dinsro.ui.core
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
   [dinsro.ui.core.transactions :as u.c.transactions]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.core.wallets.addresses :as u.c.w.addresses]
   [dinsro.ui.core.words :as u.c.words]))

(defrouter Router
  [_this {:keys [current-state pending-path-segment]
          :as props}]
  {:router-targets [u.c.addresses/NewForm
                    u.c.addresses/Report
                    u.c.blocks/Show
                    u.c.blocks/Report
                    u.c.chains/NewForm
                    u.c.chains/Report
                    u.c.chains/Show
                    u.c.connections/Report
                    u.c.connections/NewForm
                    u.c.networks/Report
                    u.c.networks/Show
                    u.c.nodes/NewForm
                    u.c.nodes/Report
                    u.c.nodes/Show
                    u.c.peers/Report
                    u.c.peers/Show
                    u.c.peers/NewForm
                    u.c.transactions/Report
                    u.c.transactions/Show
                    u.c.wallets/NewForm
                    u.c.wallets/Show
                    u.c.wallets/Report
                    u.c.w.addresses/NewForm
                    u.c.w.addresses/WalletAddressForm
                    u.c.words/Report]}
  (case current-state
    :pending (dom/div :.ui.segment "Loading... " (pr-str pending-path-segment))
    :failed (dom/div :.ui.segment "Route Failed "  (pr-str pending-path-segment))
    (dom/div {}
      (dom/div :.ui.segment
        (dom/p {} "Core router failed to match any target")
        (dom/code {} (pr-str props))))))

(defsc CorePage
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::CorePage])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["core"]}
  ((comp/factory Router) router))
