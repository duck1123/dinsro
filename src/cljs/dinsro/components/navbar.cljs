(ns dinsro.components.navbar
  (:require [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.authentication :as e.authentication]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

;; Subscriptions

(rf/reg-sub ::auth-id   (fn [db _] (get db ::e.authentication/auth-id)))

(s/def ::expanded? boolean?)
(rf/reg-sub ::expanded? (fn [db _] (get db ::expanded? false)))

;; Events

(defn toggle-navbar
  [db _]
  (update db ::expanded? not))

(defn nav-link-activated
  [{:keys [db]} _]
  {:dispatch [::toggle-navbar]})

(kf/reg-event-db ::toggle-navbar toggle-navbar)
(kf/reg-event-fx ::nav-link-activated nav-link-activated)

;; Components

(defn nav-link [title page]
  [:a.navbar-item
   {:href   (kf/path-for [page])
    :on-click #(rf/dispatch [::nav-link-activated])
    :class (when (= page @(rf/subscribe [:nav/page])) "is-active")}
   title])

(defn nav-burger
  []
  (let [expanded? @(rf/subscribe [::expanded?])]
    [:div.navbar-burger.burger
     {:role :button
      :aria-label :menu
      :aria-expanded false
      :on-click #(rf/dispatch [::toggle-navbar])
      :class (when expanded? :is-active)}
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]]))

(defn navbar []
  (let [auth-id @(rf/subscribe [::auth-id])
        expanded? @(rf/subscribe [::expanded?])]
    [:nav.navbar.is-info>div.container {:role "navigation" :aria-label "main navigation"}
     [:div.navbar-brand
      [:a.navbar-item
       {:href "/" :style {:font-weight :bold}}
       "Dinsro"]
      [nav-burger]]
     [:div.navbar-menu {:class (when expanded? :is-active)}
      [:div.navbar-start
       (when auth-id
         [:<>
          (nav-link "Accounts"    :index-accounts-page)
          (nav-link "Users"       :index-users-page)
          (nav-link "Currencies"  :index-currencies-page)
          (nav-link "Rates"       :index-rates-page)])]
      [:div.navbar-end
       (nav-link "About"          :about-page)
       (if auth-id
         [:div.navbar-item.has-dropdown.is-hoverable
          [:a.navbar-link auth-id]
          [:div.navbar-dropdown
           (nav-link "Settings"    :settings-page)
           [:a.navbar-item {:on-click #(rf/dispatch [::e.authentication/do-logout])} "Logout"]]]
         [:<>
          (nav-link "Login"       :login-page)
          (nav-link "Register"    :register-page)])]]]))
