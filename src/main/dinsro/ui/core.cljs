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
   [dinsro.ui.core.tx :as u.c.tx]
   [dinsro.ui.core.wallet-addresses :as u.c.wallet-addresses]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.core.words :as u.c.words]))

(defrouter CoreRouter
  [_this props]
  {:router-targets [u.c.addresses/CoreAddressForm
                    u.c.addresses/CoreAddressReport
                    u.c.blocks/ShowBlock
                    u.c.blocks/CoreBlockReport
                    u.c.chains/CoreChainForm
                    u.c.chains/Report
                    u.c.chains/ShowChain
                    u.c.connections/CoreNodeConnectionsReport
                    u.c.connections/NewConnectionForm
                    u.c.networks/CoreNetworkForm
                    u.c.networks/CoreNetworksReport
                    u.c.networks/ShowNetwork
                    u.c.nodes/NewCoreNodeForm
                    u.c.nodes/CoreNodesReport
                    u.c.nodes/ShowNode
                    u.c.nodes/NodeContainer
                    u.c.peers/CorePeersReport
                    u.c.peers/CorePeers2Report
                    u.c.peers/NewCorePeerForm
                    u.c.tx/CoreTxReport
                    u.c.tx/ShowTransaction
                    u.c.wallets/NewWalletForm
                    u.c.wallets/ShowWallet
                    u.c.wallets/WalletsReport
                    u.c.wallet-addresses/NewWalletAddressForm
                    u.c.wallet-addresses/WalletAddressForm
                    u.c.words/WordsReport]}
  (dom/div :.ui.segment
    (dom/p {} "Core router failed to match any target")
    (dom/code {} (pr-str props))))

(def ui-core-router (comp/factory CoreRouter))

(defsc CorePage
  [_this {:ui/keys [core-router]}]
  {:query         [{:ui/core-router (comp/get-query CoreRouter)}]
   :initial-state {:ui/core-router {}}
   :ident         (fn [] [:component/id ::CorePage])
   :route-segment ["core"]}
  (ui-core-router core-router))
