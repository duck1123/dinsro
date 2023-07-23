(ns dinsro.ui.settings.ln
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
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

(m.navbars/defmenu :settings-ln
  {::m.navbars/parent :settings
   ::m.navbars/children
   [:settings-ln-dashboard
    :settings-ln-nodes
    ;; :settings-ln-channels
    :settings-ln-payments
    :settings-ln-payreqs
    :settings-ln-remote-nodes]})

(defsc Page
  [_this {:ui/keys [nav-menu router]
          :as props}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state (fn [_]
                    {::m.navlinks/id index-page-key
                     :ui/nav-menu    (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :settings-ln})
                     :ui/router      (comp/get-initial-state Router)})
   :pre-merge     (u.loader/page-merger nil {:ui/router [Router {}]})
   :query         (fn [_]
                    [::m.navlinks/id
                     {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                     {:ui/router (comp/get-query Router)}])
   :route-segment ["ln"]}
  (let [{:keys [router-wrapper]} (css/get-classnames Page)]
    (dom/div :.ui.container
      (if nav-menu
        (u.menus/ui-nav-menu nav-menu)
        (u.debug/load-error props "settings ln nav menu"))

      (dom/div {:classes [:.nostr-page router-wrapper]}
        (if router
          (ui-router router)
          (u.debug/load-error props "settings ln router"))))))

(m.navlinks/defroute   :settings-ln
  {::m.navlinks/control       ::Page
   ::m.navlinks/label         "Lightning"
   ::m.navlinks/model-key     ::m.ln.payments/id
   ::m.navlinks/navigate-key  :settings-ln-dashboard
   ::m.navlinks/parent-key    :settings
   ::m.navlinks/router        :settings
   ::m.navlinks/required-role :user})
