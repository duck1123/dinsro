(ns dinsro.ui.settings.ln
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.settings.ln.dashboard :as u.s.ln.dashboard]
   [dinsro.ui.settings.ln.nodes :as u.s.ln.nodes]
   [dinsro.ui.settings.ln.payments :as u.s.ln.payments]
   [dinsro.ui.settings.ln.payreqs :as u.s.ln.payreqs]))

(defrouter Router
  [_this _props]
  {:router-targets [u.s.ln.dashboard/Dashboard
                    u.s.ln.nodes/Report
                    u.s.ln.nodes/Show
                    u.s.ln.payments/Report
                    u.s.ln.payments/Show
                    u.s.ln.payreqs/NewForm
                    u.s.ln.payreqs/Report
                    u.s.ln.payreqs/Show]}
  (dom/div {} "Ln router"))

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [nav-menu router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state
   (fn [_]
     {:ui/nav-menu (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :settings-ln})
      :ui/router   (comp/get-initial-state Router)})
   :query         [{:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["ln"]}
  (let [{:keys [router-wrapper]} (css/get-classnames Page)]
    (dom/div :.ui.container
      (u.menus/ui-nav-menu nav-menu)
      (dom/div {:classes [:.nostr-page router-wrapper]}
        (ui-router router)))))
