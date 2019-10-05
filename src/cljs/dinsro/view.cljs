(ns dinsro.view
  (:require [dinsro.components.user :refer [users-page]]
            [kee-frame.core :as kf]
            [markdown.core :refer [md->html]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(rf/reg-sub
 :navbar-expanded
 (fn [db _] (get db :navbar-expanded)))

(rf/reg-event-db
 :toggle-navbar
 (fn [db [_ _]]
   ;; TODO: I think this is a stream
   (assoc db :navbar-expanded (not (get db :navbar-expanded)))))

(defn nav-link [title page]
  [:a.navbar-item
   {:href   (kf/path-for [page])
    :class (when (= page @(rf/subscribe [:nav/page])) "is-active")}
   title])

(defn navbar []
  (let [expanded? @(rf/subscribe [:navbar-expanded])]
    [:nav.navbar.is-info>div.container {:role "navigation" :aria-label "main navigation"}
     [:div.navbar-brand
      [:a.navbar-item
       {:href "/" :style {:font-weight :bold}}
       "Dinsro"]
      [:div.navbar-burger.burger
       {:role :button
        :aria-label :menu
        :aria-expanded false
        :on-click #(rf/dispatch [:toggle-navbar])
        :class (when expanded? :is-active)}
       [:span][:span][:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when expanded? :is-active)}
      [:div.navbar-start
       [nav-link "Home" :home]]
      [:div.navbar-end
       [nav-link "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn home-page []
  [:section.section>div.container>div.content
   (when-let [docs @(rf/subscribe [:docs])]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :home home-page
    :about about-page
    nil [:div ""]]])
