(ns dinsro.views.login
  (:require [ajax.core :as ajax]
            [cemerick.url :as url]
            [clojure.string :as string]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.authentication :as e.authentication]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub      ::email       (fn [db _] (get db ::email "bob@example.com")))
(rf/reg-event-db :change-email (fn [db [_ email]] (assoc db ::email email)))

(rf/reg-sub      ::password       (fn [db _] (get db ::password "hunter2")))
(rf/reg-event-db :change-password (fn [db [_ password]] (assoc db ::password password)))

(rf/reg-sub
 ::login-data
 :<- [::email]
 :<- [::password]
 (fn [[email password] b]
   {:email email :password password}))

(rf/reg-sub
 :login-disabled?
 (fn [db _] (rf/subscribe [:state]))
 (fn [state _] (not= state :ready)))

(rf/reg-sub
 :no-email
 (fn [db _] (rf/subscribe [::email]))
 (fn [email _] (string/blank? email)))

(kf/reg-event-db
 :login-no-email
 (fn-traced [db [_ _]]
   (assoc db :no-email true)))

(kf/reg-event-fx
 :login-click
 (fn [_ _]
   (let [data @(rf/subscribe [::login-data])]
     {:dispatch [::e.authentication/do-authenticate data]})))

(defn login-form
  []
  [:form.is-centered
   [c/email-input "Email" ::email :change-email]
   [c/password-input "Password" ::password :change-password]
   [c/primary-button "Login" :login-click]])

(defn page [match]
  (let [{:keys [query-string]} match
        return-to (get (url/query->map query-string) "return-to")]
    [:section.section>div.container>div.content
     [:h1 "Login"]
     [:p (str @(rf/subscribe [::login-data]))]
     [:div.container
      [:p "Return To: " return-to]
      [login-form]]]))
