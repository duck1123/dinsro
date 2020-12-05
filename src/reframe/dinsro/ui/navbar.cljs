(ns dinsro.ui.navbar
  (:require
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.navbar :as e.navbar]
   [dinsro.events.users :as e.users]
   [dinsro.model.users :as m.users]
   [dinsro.specs.events.forms.settings :as s.e.f.settings]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.debug :as u.debug]
   [taoensso.timbre :as timbre]))

(defn nav-link
  [store title page]
  [:a.navbar-item
   {:href   (st/path-for store [page])
    :on-click #(st/dispatch store [::e.navbar/nav-link-activated])
    :class (when (= page @(st/subscribe store [:nav/page])) :is-active)}
   title])

(defn nav-burger
  [store]
  (let [expanded? @(st/subscribe store [::e.navbar/expanded?])]
    [:div.navbar-burger.burger
     {:role :button
      :aria-label :menu
      :aria-expanded false
      :onClick #(st/dispatch store [::e.navbar/toggle-navbar])
      :className (when expanded? :is-active)}
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]]))

(defn debug-button
  [store]
  [:a.navbar-item
   {:on-click #(st/dispatch store [::e.debug/toggle-shown?])}
   ""])

(defn navbar
  [store]
  (let [auth-id @(st/subscribe store [::e.authentication/auth-id])
        expanded? @(st/subscribe store [::e.navbar/expanded?])]
    [:nav.navbar.is-info>div.container {:role "navigation" :aria-label "main navigation"}
     [:div.navbar-brand
      [:a.navbar-item
       {:href "/" :style {:font-weight :bold}}
       "Dinsro"]
      [nav-burger store]]
     [:div.navbar-menu {:class (when expanded? :is-active)}
      [:div.navbar-start
       (when auth-id
         [:<>
          (nav-link store (tr [:accounts]) :index-accounts-page)
          (nav-link store (tr [:transactions]) :index-transactions-page)])]
      [:div.navbar-end
       (when @(st/subscribe store [::e.debug/enabled?]) [debug-button])
       (if auth-id
         [:div.navbar-item.has-dropdown.is-hoverable
          [:a.navbar-link (::m.users/name @(st/subscribe store [::e.users/item auth-id]))]
          [:div.navbar-dropdown
           (nav-link store (tr [:settings]) :settings-page)
           (u.debug/hide store (nav-link store (tr [:currencies]) :index-currencies-page))
           (nav-link store (tr [:admin]) :admin-page)
           (u.debug/hide store (nav-link store (tr [:rate-sources]) :index-rate-sources-page))
           (u.debug/hide store (nav-link store (tr [:rates]) :index-rates-page))
           (u.debug/hide store (nav-link store (tr [:categories]) :index-categories-page))
           (u.debug/hide store (nav-link store (tr [:users]) :index-users-page))
           [:a.navbar-item
            {:on-click #(st/dispatch store [::e.authentication/do-logout])}
            (tr [:logout])]]]
         [:<>
          ;; FIXME: Do not show this section when settings are not loaded
          (nav-link store (tr [:login]) :login-page)
          (when @(st/subscribe store [::s.e.f.settings/allow-registration])
            (nav-link store (tr [:register]) :register-page))])]]]))
