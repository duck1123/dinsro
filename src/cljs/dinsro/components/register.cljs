(ns dinsro.components.register
  (:require [ajax.core :as ajax]
            [reagent.core :as r]))

(def default-registration-info
  {:email "bob@example.com"
   :password "b"
   :confirm-password "a"})

(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn do-register
  [params]
  (fn [event]
    (.preventDefault event)
    ;; TODO: validate password and confirm match
    (ajax/POST "/api/v1/register"
               {:params (dissoc params :confirm-password)
                :handler handler
                :error-handler error-handler})))

(defn on-submit
  [e]
  (.preventDefault e))

(defn registration-page-
  [s]
  [:div
   [:h1 "Registration Page"]
   [:form {:on-submit on-submit}
    [:div
     [:p s]]
    [:div
     #_[ui/text-field
      {:id "name"
       :label "name"
       :auto-complete "name"
       :value (:name @s "")
       :on-change (fn [e] (swap! s assoc :name e.target.value))
       :margin "normal"}]]
    [:div
     #_[ui/text-field
      {:id "email"
       :label "email"
       :auto-complete "email"
       :value (:email @s "")
       :on-change (fn [e] (swap! s assoc :email e.target.value))
       :margin "normal"}]]
    [:div
     #_[ui/text-field
      {:id "password"
       :label "Password"
       :type "password"
       :value (:password @s)
       :autoComplete "current-password"
       :on-change (fn [e] (swap! s assoc :password e.target.value))
       :margin "normal"}]]
    [:div
     #_[ui/text-field
      {:id "confirmPassword"
       :label "Confirm Password"
       :type "password"
       :value (:confirm-password @s)
       :autoComplete "current-password"
       :on-change (fn [e] (swap! s assoc :confirm-password e.target.value))
       :margin "normal"}]]
    [:div
     #_[ui/button
      {:variant "raised"
       :on-click (fn [e]
                   (js/console.log "clicked" @s)
                   ((do-register @s) e))
       :color "primary"}
      "Submit"]]]])

(defn registration-page
  []
  (let [s (r/atom default-registration-info)]
    (partial registration-page s)))
