(ns dinsro.views.login
  (:require [ajax.core :as ajax]
            [cemerick.url :as url]
            [clojure.string :as string]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.forms.login :as c.f.login]
            [dinsro.events.authentication :as e.authentication]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(def default-email "bob@example.com")
(def default-password "hunter2")

;; (rf/reg-sub      ::email       (fn [db _] (get db ::email default-email)))
;; (kf/reg-event-db :change-email (fn [db [email]] (assoc db ::email email)))

;; (rf/reg-sub      ::password       (fn [db _] (get db ::password default-password)))
;; (kf/reg-event-db :change-password (fn [db [password]] (assoc db ::password password)))

;; (rf/reg-sub
;;  ::login-data
;;  :<- [::email]
;;  :<- [::password]
;;  (fn [[email password] b]
;;    {:email email :password password}))

;; (rf/reg-sub
;;  :login-disabled?
;;  (fn [db _] (rf/subscribe [:state]))
;;  (fn [state _] (not= state :ready)))

;; (rf/reg-sub
;;  :no-email
;;  (fn [db _] (rf/subscribe [::email]))
;;  (fn [email _] (string/blank? email)))

;; (kf/reg-event-db
;;  :login-no-email
;;  (fn-traced [db [_ _]]
;;    (assoc db :no-email true)))

(defn page [match]
  (let [{:keys [query-string]} match
        return-to (get (url/query->map query-string) "return-to")]
    [:section.section>div.container>div.content
     [:h1 "Login"]
     [:p "Authenticated: " @(rf/subscribe [::e.authentication/auth-id])]
     [:div.container
      [:p "Return To: " return-to]
      [c.f.login/login-form return-to]]]))
