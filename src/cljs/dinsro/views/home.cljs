(ns dinsro.views.home
  (:require
   [dinsro.components :as c]
   [dinsro.components.account-picker :as c.account-picker]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.users :as e.users]
   [dinsro.spec.users :as s.users]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]))

(defn init-page
  [_ _]
  {:document/title "Home"
   :dispatch-n [[::e.accounts/do-fetch-index]
                [::e.currencies/do-fetch-index]
                [::e.users/do-fetch-index]]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :home-page)
  :start [::init-page]})

(defn page
  []
  (let [auth-id @(rf/subscribe [::e.authentication/auth-id])]
    [:section.section>div.container>div.content
     (if auth-id
       [:<>
        (if-let [user @(rf/subscribe [::e.users/item auth-id])]
          (let [name (some-> user ::s.users/name)]
            [:h1.title "Welcome, " name])
          [:div.box.is-danger "User is not loaded"])
        [c.account-picker/section]]
       [:div.box
        [:h1 (tr [:home-page])]
        [:p "Not authenticated. " [:a {:href (kf/path-for [:login-page])} "login"]]])]))
