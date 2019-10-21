(ns dinsro.components.navbar
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.logout :as c.logout]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

;; Subscriptions

(rf/reg-sub :authenticated   (fn [db _] (get db :authenticated false)))
(rf/reg-sub :navbar-expanded (fn [db _] (get db :navbar-expanded false)))

;; Events

(kf/reg-event-db
 :toggle-navbar
 (fn-traced [db _]
   (update db :navbar-expanded not)))

(kf/reg-event-fx
 :nav-link-activated
 (fn-traced
   [{:keys [db]} _]
   {:dispatch [:toggle-navbar]}))

;; Components

(defn nav-link [title page]
  [:a.navbar-item
   {:href   (kf/path-for [page])
    :on-click #(rf/dispatch [:nav-link-activated])
    :class (when (= page @(rf/subscribe [:nav/page])) "is-active")}
   title])

(defn nav-burger
  []
  (let [expanded? @(rf/subscribe [:navbar-expanded])]
    [:div.navbar-burger.burger
     {:role :button
      :aria-label :menu
      :aria-expanded false
      :on-click #(rf/dispatch [:toggle-navbar])
      :class (when expanded? :is-active)}
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]]))

(defn navbar []
  (let [authenticated @(rf/subscribe [:authenticated])
        expanded? @(rf/subscribe [:navbar-expanded])]
    [:nav.navbar.is-info>div.container {:role "navigation" :aria-label "main navigation"}
     [:div.navbar-brand
      [:a.navbar-item
       {:href "/" :style {:font-weight :bold}}
       "Dinsro"]
      [nav-burger]]
     [:div.navbar-menu {:class (when expanded? :is-active)}
      [:div.navbar-start
       (when authenticated
         [:<>
          (nav-link "Accounts"    :index-accounts-page)
          (nav-link "Users"       :index-users-page)
          (nav-link "Currencies"  :index-currencies-page)
          (nav-link "Rates"       :index-rates-page)])]
      [:div.navbar-end
       (nav-link "About"          :about-page)
       (if authenticated
         [:div.navbar-item.has-dropdown.is-hoverable
          [:a.navbar-link authenticated]
          [:div.navbar-dropdown
           (nav-link "Settings"    :settings-page)
           [:a.navbar-item {:on-click #(rf/dispatch [::c.logout/do-logout])} "Logout"]]]
         [:<>
          (nav-link "Login"       :login-page)
          (nav-link "Register"    :register-page)])]]]))
