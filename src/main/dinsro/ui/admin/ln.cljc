(ns dinsro.ui.admin.ln
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.admin.ln.accounts :as u.a.ln.accounts]
   [dinsro.ui.admin.ln.channels :as u.a.ln.channels]
   [dinsro.ui.admin.ln.dashboard :as u.a.ln.dashboard]
   [dinsro.ui.admin.ln.invoices :as u.a.ln.invoices]
   [dinsro.ui.admin.ln.nodes :as u.a.ln.nodes]
   [dinsro.ui.admin.ln.payments :as u.a.ln.payments]
   [dinsro.ui.admin.ln.payreqs :as u.a.ln.payreqs]
   [dinsro.ui.admin.ln.peers :as u.a.ln.peers]
   [dinsro.ui.admin.ln.remote-nodes :as u.a.ln.remote-nodes]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

(def index-page-key :admin-ln)

(defrouter Router
  [_this _props]
  {:router-targets [u.a.ln.dashboard/IndexPage
                    u.a.ln.accounts/IndexPage
                    u.a.ln.channels/IndexPage
                    u.a.ln.invoices/IndexPage
                    u.a.ln.nodes/IndexPage
                    u.a.ln.payments/IndexPage
                    u.a.ln.payreqs/IndexPage
                    u.a.ln.peers/IndexPage
                    u.a.ln.remote-nodes/IndexPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu :admin-ln
  {::m.navbars/parent :admin
   ::m.navbars/router ::Router
   ::m.navbars/children
   [:admin-ln-dashboard
    :admin-ln-accounts
    :admin-ln-channels
    :admin-ln-invoices
    :admin-ln-nodes
    :admin-ln-payreqs
    :admin-ln-peers
    :admin-ln-remote-nodes]})

(defsc IndexPage
  [_this {:ui/keys [router vertical-menu] :as props}]
  {:ident         (fn [] [::m.navlinks/id :admin-ln])
   :initial-state (fn [_]
                    {::m.navlinks/id   index-page-key
                     :ui/router        (comp/get-initial-state Router)
                     :ui/vertical-menu (comp/get-initial-state u.menus/VerticalMenu {::m.navbars/id :admin-ln})})
   :pre-merge     (u.loader/page-merger nil
                    {:ui/router        [Router {}]
                     :ui/vertical-menu [u.menus/VerticalMenu {::m.navbars/id :admin-ln}]})
   :query         [::m.navlinks/id
                   {:ui/router (comp/get-query Router)}
                   {:ui/vertical-menu (comp/get-query u.menus/VerticalMenu)}]
   :route-segment ["ln"]}
  (log/info :IndexPage/starting {:props props})
  (comp/fragment
   (dom/div :.ui.grid
     (dom/div :.ui.four.wide.column
       (if vertical-menu
         (u.menus/ui-vertical-menu vertical-menu)
         (ui-segment {} "Failed to load menu")))
     (dom/div :.ui.twelve.wide.column
       (if router
         (ui-router router)
         (ui-segment {} "Failed to load router"))))))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "LN"
   ::m.navlinks/parent-key    :admin
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})
