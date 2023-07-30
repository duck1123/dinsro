(ns dinsro.ui.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.admin.accounts :as u.a.accounts]
   [dinsro.ui.admin.categories :as u.a.categories]
   [dinsro.ui.admin.contacts :as u.a.contacts]
   [dinsro.ui.admin.core :as u.a.core]
   [dinsro.ui.admin.currencies :as u.a.currencies]
   [dinsro.ui.admin.debits :as u.a.debits]
   [dinsro.ui.admin.ln :as u.a.ln]
   [dinsro.ui.admin.models :as u.a.models]
   [dinsro.ui.admin.navbars :as u.a.navbars]
   [dinsro.ui.admin.navlinks :as u.a.navlinks]
   [dinsro.ui.admin.nostr :as u.a.nostr]
   [dinsro.ui.admin.rate-sources :as u.a.rate-sources]
   [dinsro.ui.admin.rates :as u.a.rates]
   [dinsro.ui.admin.transactions :as u.a.transactions]
   [dinsro.ui.admin.users :as u.a.users]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

(def debug-route false)
(def index-page-key :admin)

(defrouter Router
  [_this {:keys [current-state route-factory route-props router-state] :as props}]
  {:always-render-body? true
   :router-targets      [u.a.accounts/IndexPage
                         u.a.accounts/ShowPage
                         u.a.categories/IndexPage
                         u.a.categories/ShowPage
                         u.a.contacts/IndexPage
                         u.a.contacts/ShowPage
                         u.a.core/IndexPage
                         u.a.currencies/IndexPage
                         u.a.currencies/ShowPage
                         u.a.debits/IndexPage
                         u.a.debits/ShowPage
                         u.a.ln/IndexPage
                         u.a.navbars/IndexPage
                         u.a.navlinks/IndexPage
                         u.a.nostr/IndexPage
                         u.a.rates/IndexPage
                         u.a.rates/ShowPage
                         u.a.rate-sources/IndexPage
                         u.a.rate-sources/ShowPage
                         u.a.transactions/IndexPage
                         u.a.transactions/NewForm
                         u.a.transactions/ShowPage
                         u.a.users/IndexPage
                         u.a.users/ShowPage]}
  (log/debug :Router/starting {:props props})
  (dom/div :.admin-router-outer {}
    (case current-state
      :pending
      (dom/div {} "Loading...")

      :failed
      (u.debug/load-error props "Admin router")

      :routed
      (route-factory route-props)

      ;; default will be used when the current state isn't yet set
      (dom/div :.admin-router
        (dom/div "No route selected.")
        (u.debug/log-props {:current-state current-state :router-state router-state})))
    (when debug-route
      (ui-segment {}
        (dom/h3 {} "Admin Router")
        (u.debug/log-props props)))))

(def ui-router (comp/factory Router))

;; The top sub menu on admin pages
(m.navbars/defmenu :admin
  {::m.navbars/parent :root
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.users/index-page-key
    u.a.core/index-page-key
    u.a.ln/index-page-key
    u.a.nostr/index-page-key
    u.a.categories/index-page-key
    u.a.accounts/index-page-key
    u.a.currencies/index-page-key
    u.a.transactions/index-page-key
    u.a.debits/index-page-key
    u.a.rate-sources/index-page-key
    u.a.rates/index-page-key
    u.a.models/index-page-key
    u.a.navbars/index-page-key
    u.a.navlinks/index-page-key]})

(def debug-props false)

(defsc IndexPage
  [_this {:ui/keys [nav-menu router] :as props}]
  {:ident         (fn [] [::m.navlinks/id :admin])
   :initial-state (fn [props]
                    (log/trace :IndexPage/initial-state {:props props})
                    {::m.navlinks/id :admin
                     :ui/nav-menu    (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :admin})
                     :ui/router      (comp/get-initial-state Router)})
   :query         [::m.navlinks/id
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["admin"]}
  (log/debug :IndexPage/starting {:props props})
  (dom/div :.admin-page
    (if nav-menu
      (u.menus/ui-nav-menu nav-menu)
      (u.debug/load-error props "admin nav menu"))
    (if router
      (ui-router router)
      (u.debug/load-error props "admin router"))
    (when debug-props
      (u.debug/log-props props))))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/description   "Admin root page"
   ::m.navlinks/label         "Admin"
   ::m.navlinks/navigate-key  u.a.users/index-page-key
   ::m.navlinks/parent-key    :root
   ::m.navlinks/router        :root
   ::m.navlinks/required-role :admin})
