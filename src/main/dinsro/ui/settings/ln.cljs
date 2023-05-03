(ns dinsro.ui.settings.ln
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.settings.ln.nodes :as u.s.ln.nodes]
   [dinsro.ui.settings.ln.payments :as u.s.ln.payments]
   [dinsro.ui.settings.ln.payreqs :as u.s.ln.payreqs]))

(def menu-items
  [{:key   "dashboard"
    :name  "Dashboard"
    :route "dinsro.ui.settings/Dashboard"}
   {:key   "nodes"
    :name  "Nodes"
    :route "dinsro.ui.settings.ln.nodes/Report"}
   {:key   "channels"
    :name  "Channels"
    :route "dinsro.ui.settings.ln.channels/Report"}
   {:key   "payments"
    :name  "Payments"
    :route "dinsro.ui.settings.ln.payments/Report"}
   {:name  "Payreqs"
    :key   "payreqs"
    :route "dinsro.ui.settings.ln.payreqs/Report"}
   {:name  "Remote Nodes"
    :key   "remote-nodes"
    :route "dinsro.ui.settings.ln.remote-nodes/Report"}])

(defsc Dashboard
  [_this _props]
  {}
  (dom/div {} "Dashboard"))

(defrouter Router
  [_this _props]
  {:router-targets [u.s.ln.nodes/Report
                    u.s.ln.nodes/Show
                    u.s.ln.payments/Report
                    u.s.ln.payments/Show
                    u.s.ln.payreqs/NewForm
                    u.s.ln.payreqs/Report
                    u.s.ln.payreqs/Show]}
  (dom/div {} "Ln router"))

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["ln"]}
  (let [{:keys [router-wrapper]} (css/get-classnames Page)]
    (dom/div :.ui.container
      (u.links/ui-nav-menu {:id nil :menu-items menu-items})
      (dom/div {:classes [:.nostr-page router-wrapper]}
        ((comp/factory Router) router)))))
