(ns dinsro.ui.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.ui.admin.accounts :as u.a.accounts]
   [dinsro.ui.admin.categories :as u.a.categories]
   [dinsro.ui.admin.core :as u.a.core]
   [dinsro.ui.admin.currencies :as u.a.currencies]
   [dinsro.ui.admin.debits :as u.a.debits]
   [dinsro.ui.admin.ln :as u.a.ln]
   [dinsro.ui.admin.nostr :as u.a.nostr]
   [dinsro.ui.admin.rate-sources :as u.a.rate-sources]
   [dinsro.ui.admin.rates :as u.a.rates]
   [dinsro.ui.admin.transactions :as u.a.transactions]
   [dinsro.ui.admin.users :as u.a.users]
   [dinsro.ui.menus :as u.menus]
   [taoensso.timbre :as log]))

(defrouter Router
  [_this {:keys [current-state]}]
  {:router-targets [u.a.accounts/Report
                    u.a.categories/NewForm
                    u.a.categories/Report
                    u.a.core/Page
                    u.a.currencies/Report
                    u.a.debits/Report
                    u.a.ln/Page
                    u.a.nostr/Page
                    u.a.rates/Show
                    u.a.rates/Report
                    u.a.rate-sources/Report
                    u.a.transactions/Report
                    u.a.users/Report
                    u.a.users/Show
                    u.a.users/UserForm]}
  (dom/div :.admin-router
    (case current-state
      :pending (dom/div {} "Loading...")
      :failed  (dom/div {} "Failed!")
      ;; default will be used when the current state isn't yet set
      (dom/div {}
        (dom/div "No route selected.")))))

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [nav-menu router]}]
  {:ident         (fn [] [:page/id ::Page])
   :initial-state
   (fn [props]
     (log/trace :Page/initial-state {:props props})
     {:ui/nav-menu (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :admin})
      :ui/router   (comp/get-initial-state Router)})
   :query         [{:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["admin"]}
  (dom/div :.admin-page
    (u.menus/ui-nav-menu nav-menu)
    (ui-router router)))
