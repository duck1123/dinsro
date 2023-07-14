(ns dinsro.ui.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.core.addresses :as u.c.addresses]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [dinsro.ui.core.chains :as u.c.chains]
   [dinsro.ui.core.mnemonics :as u.c.mnemonics]
   [dinsro.ui.core.networks :as u.c.networks]
   [dinsro.ui.core.nodes :as u.c.nodes]
   [dinsro.ui.core.peers :as u.c.peers]
   [dinsro.ui.core.transactions :as u.c.transactions]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.core.wallets.addresses :as u.c.w.addresses]
   [dinsro.ui.core.words :as u.c.words]))

(def index-page-key :core)

(defrouter Router
  [_this {:keys [current-state pending-path-segment]
          :as props}]
  {:router-targets [u.c.addresses/NewForm
                    u.c.addresses/IndexPage
                    u.c.blocks/ShowPage
                    u.c.blocks/IndexPage
                    u.c.chains/NewForm
                    u.c.chains/IndexPage
                    u.c.chains/ShowPage
                    u.c.mnemonics/IndexPage
                    u.c.networks/IndexPage
                    u.c.networks/ShowPage
                    u.c.nodes/NewForm
                    u.c.nodes/IndexPage
                    u.c.nodes/ShowPage
                    u.c.peers/IndexPage
                    u.c.peers/ShowPage
                    u.c.peers/NewForm
                    u.c.transactions/IndexPage
                    u.c.transactions/ShowPage
                    u.c.wallets/NewForm
                    u.c.wallets/ShowPage
                    u.c.wallets/IndexPage
                    u.c.w.addresses/NewForm
                    u.c.w.addresses/WalletAddressForm
                    u.c.words/IndexPage]}
  (case current-state
    :pending (dom/div :.ui.segment "Loading... " (pr-str pending-path-segment))
    :failed (dom/div :.ui.segment "Route Failed "  (pr-str pending-path-segment))
    (dom/div {}
      (dom/div :.ui.segment
        (dom/p {} "Core router failed to match any target")
        (dom/code {} (pr-str props))))))

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/router      {}}
   :query         [::m.navlinks/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["core"]}
  (ui-router router))

(m.navlinks/defroute    :core
  {::m.navlinks/control       ::Page
   ::m.navlinks/description   "Router for core"
   ::m.navlinks/label         "Core"
   ::m.navlinks/navigate-key  :core-dashboard
   ::m.navlinks/parent-key    :root
   ::m.navlinks/router        :root
   ::m.navlinks/required-role :guest})
