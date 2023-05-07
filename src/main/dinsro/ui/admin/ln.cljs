(ns dinsro.ui.admin.ln
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.menus :as me]
   [dinsro.ui.admin.ln.accounts :as u.a.ln.accounts]
   [dinsro.ui.admin.ln.channels :as u.a.ln.channels]
   [dinsro.ui.admin.ln.invoices :as u.a.ln.invoices]
   [dinsro.ui.admin.ln.nodes :as u.a.ln.nodes]
   [dinsro.ui.admin.ln.payments :as u.a.ln.payments]
   [dinsro.ui.admin.ln.payreqs :as u.a.ln.payreqs]
   [dinsro.ui.admin.ln.peers :as u.a.ln.peers]
   [dinsro.ui.admin.ln.remote-nodes :as u.a.ln.remote-nodes]
   [dinsro.ui.links :as u.links]))

(def router-key :dinsro.ui.admin/Router)

(defsc Dashboard
  [_this _props]
  {:ident         (fn [] [:component/id ::Dashboard])
   :initial-state {}
   :query         [[::dr/id router-key]]
   :route-segment ["dashboard"]}
  (dom/div {}
    (dom/h1 "Dashboard")))

(defrouter Router
  [_this _props]
  {:router-targets [Dashboard
                    u.a.ln.accounts/Report
                    u.a.ln.channels/Report
                    u.a.ln.invoices/Report
                    u.a.ln.nodes/Report
                    u.a.ln.payments/Report
                    u.a.ln.payreqs/Report
                    u.a.ln.peers/Report
                    u.a.ln.remote-nodes/Report]})

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["ln"]}
  (comp/fragment
   (dom/div :.ui.grid
     (dom/div :.ui.four.wide.column
       (u.links/ui-vertical-menu {:menu-items me/admin-ln-menu-items :id nil}))
     (dom/div :.ui.twelve.wide.column
       ((comp/factory Router) router)))))
