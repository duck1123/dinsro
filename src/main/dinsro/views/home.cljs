(ns dinsro.views.home
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.users :as e.users]
   [dinsro.specs.users :as s.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.account-picker :as u.account-picker]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:document/title "Home"
   :dispatch-n [[::e.accounts/do-fetch-index]
                [::e.currencies/do-fetch-index]
                [::e.users/do-fetch-index]]})

(defn page
  [store _match]
  (let [auth-id @(st/subscribe store [::e.authentication/auth-id])]
    [:section.section>div.container>div.content
     (if auth-id
       [:<>
        (if-let [user @(st/subscribe store [::e.users/item auth-id])]
          (let [name (some-> user ::s.users/name)]
            [:h1.title "Welcome, " name])
          [:div.box.is-danger "User is not loaded"])
        [u.account-picker/section store]]
       [:div.box
        [:h1 (tr [:home-page])]
        [:p "Not authenticated. " [:a {:href (st/path-for store [:login-page])} "login"]]])]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page-controller
   {:params (u/filter-page :home-page)
    :start [::init-page]})

  store)
