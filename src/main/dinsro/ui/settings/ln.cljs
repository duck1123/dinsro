(ns dinsro.ui.settings.ln
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.settings.ln.dashboard :as u.s.ln.dashboard]
   [dinsro.ui.settings.ln.nodes :as u.s.ln.nodes]
   [dinsro.ui.settings.ln.payments :as u.s.ln.payments]
   [dinsro.ui.settings.ln.payreqs :as u.s.ln.payreqs]
   [dinsro.ui.settings.ln.remote-nodes :as u.s.ln.remote-nodes]))

(def index-page-key :settings-ln)

(defrouter Router
  [_this _props]
  {:router-targets [u.s.ln.dashboard/Page
                    u.s.ln.nodes/IndexPage
                    u.s.ln.nodes/ShowPage
                    u.s.ln.payments/IndexPage
                    u.s.ln.payments/ShowPage
                    u.s.ln.payreqs/NewForm
                    u.s.ln.payreqs/IndexPage
                    u.s.ln.payreqs/ShowPage
                    u.s.ln.remote-nodes/IndexPage]}
  (dom/div {} "Ln router"))

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [nav-menu router]}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state (fn [_]
                    {::m.navlinks/id index-page-key
                     :ui/nav-menu    (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :settings-ln})
                     :ui/router      (comp/get-initial-state Router)})
   :pre-merge     (u.loader/page-merger nil {:ui/router [Router {}]})
   :query         [::m.navlinks/id
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["ln"]}
  (let [{:keys [router-wrapper]} (css/get-classnames Page)]
    (dom/div :.ui.container
      (if nav-menu
        (u.menus/ui-nav-menu nav-menu)
        (dom/div :.ui.segment "Failed to load menu"))
      (dom/div {:classes [:.nostr-page router-wrapper]}
        (if router
          (ui-router router)
          (dom/div :.ui.segment "Failed to load router"))))))
