(ns dinsro.views.login
  (:require [ajax.core :as ajax]
            [clojure.string :as string]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::email ::email)
(rf/reg-sub ::password ::password)

(rf/reg-sub :state :state)

(rf/reg-sub
 :failure
 (fn [db _] (rf/subscribe [:state]))
 (fn [state _] (case state
                 :email-required "email required"
                 :password-required "password required"
                 :user-not-exist "user not found"
                 :invalid-password "invalid password"
                 nil)))

(rf/reg-sub
 :login-disabled?
 (fn [db _] (rf/subscribe [:state]))
 (fn [state _] (not= state :ready)))

(rf/reg-sub
 :no-email
 (fn [db _] (rf/subscribe [::email]))
 (fn [email _] (string/blank? email)))

(def login-state-machine
  {nil         {:init               :ready}
   :ready      {:login-no-password  :password-required
                :login-no-email     :email-required
                :try-login          :logging-in}
   :logging-in {:login-bad-password :invalid-password}})

(defn next-state
  [state-machine current-state transition]
  (get-in state-machine [current-state transition]))

(defn update-next-state
  [db event]
  (update db :state (partial next-state login-state-machine) event))

(defn handle-next-state
  [db [event _]]
  (update-next-state db event))

(rf/reg-event-db
 :change-email
 (fn-traced [db [_ email]]
   (assoc db ::email email)))

(rf/reg-event-db
 :change-password
 (fn-traced [db [_ password]]
   (assoc db ::password password)))

(rf/reg-event-db
 :login-no-email
 (fn-traced [db [_ _]]
   (assoc db :no-email true)))

(rf/reg-event-fx
 :login-failed
 (fn [{:keys [db]} event]
   {:db (-> db
            (assoc :login-failed true)
            (assoc :loading false))}))

(rf/reg-event-fx
 :submit-login
 (fn [_ [_ data]]
   {:http-xhrio
    {:method :post
     :uri "/api/v1/authenticate"
     :params data
     :timeout 8000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success [:login-succeeded]
     :on-failure [:login-failed]}}))

(rf/reg-event-fx
 :login-click
 (fn-traced [{:keys [db]} _]
   (let [email (::email db)
         password (::password db)]
     {:db (assoc db :loading true)
      :dispatch [:submit-login {:email email :password password}]})))

(defn page []
  [:div.section
   [:div.container
    [:form.is-centered
     [:div.field
      [:label.label "Email"]
      [:div.control
       [:input.input
        {:value @(rf/subscribe [::email])
         :on-change #(rf/dispatch [:change-email (-> % .-target .-value)])}]]
      (when-let [failure @(rf/subscribe [:no-email])]
        [:p.help.is-danger "This email is invalid"])]
     [:div.field
      [:label.label "Password"]
      [:div.control
       [:input.input
        {:value @(rf/subscribe [::password])
         :on-change #(rf/dispatch [:change-password (-> % .-target .-value)])
         :type "password"}]]]
     [:div.field
      [:div.control
       [:a.button.is-primary
        {:on-click (fn [e] (rf/dispatch [:login-click]))}
        "Login"]]]]]])
