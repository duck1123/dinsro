(ns dinsro.view
  (:require [dinsro.components.login-page :refer [login-page]]
            [kee-frame.core :as kf]
            [markdown.core :refer [md->html]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

;; Subscriptions

(rf/reg-sub
 :navbar-expanded
 (fn [db _] (get db :navbar-expanded)))

(rf/reg-sub
 :authenticated
 (fn [db _] (get db :authenticated)))

;; Events

(rf/reg-event-db
 :toggle-navbar
 (fn [db [_ _]]
   ;; TODO: I think this is a stream
   (assoc db :navbar-expanded (not (get db :navbar-expanded)))))

(rf/reg-event-db
 :toggle-auth
 (fn [db [_ _]]
   (timbre/warn "This shouldn't be available")
   (update-in db [:authenticated] not)))

;; Components

(defn nav-link [title page]
  [:a.navbar-item
   {:href   (kf/path-for [page])
    :class (when (= page @(rf/subscribe [:nav/page])) "is-active")}
   title])

(defn auth-toggle-button
  []
  (let [authenticated @(rf/subscribe [:authenticated])]
    [:a.navbar-item
     {:role :button
      :on-click #(rf/dispatch [:toggle-auth])}
     (if authenticated "in" "out")]))

(defn navbar []
  (let [authenticated @(rf/subscribe [:authenticated])
        expanded? @(rf/subscribe [:navbar-expanded])]
    [:nav.navbar.is-info>div.container {:role "navigation" :aria-label "main navigation"}
     [:div.navbar-brand
      [:a.navbar-item
       {:href "/" :style {:font-weight :bold}}
       "Dinsro"]
      [auth-toggle-button]
      [:div.navbar-burger.burger
       {:role :button
        :aria-label :menu
        :aria-expanded false
        :on-click #(rf/dispatch [:toggle-navbar])
        :class (when expanded? :is-active)}
       [:span {:aria-hidden true}]
       [:span {:aria-hidden true}]
       [:span {:aria-hidden true}]]]
     [:div.navbar-menu {:class (when expanded? :is-active)}
      [:div.navbar-start]
      [:div.navbar-end
       [nav-link "About" :about]
       (if (not authenticated)
         [nav-link (str "Login " authenticated) :login])]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn home-page []
  [:section.section>div.container>div.content
   [:div "Home Page"]])

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :home home-page
    :about about-page
    :login login-page
    nil [:div ""]]])
