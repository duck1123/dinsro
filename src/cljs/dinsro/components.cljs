(ns dinsro.components
  (:require [dinsro.events.currencies :as e.currencies]
            [re-frame.core :as rf]))

(def target-value #(-> % .-target .-value))

(defn reg-field
  [key default]
  (rf/reg-sub key (fn [db _] (get db key default))))

(defn input-field
  [label field change-handler type]
  [:div.field
   [:div.control
    [:label.label label]
    [:input.input
     {:type type
      :value @(rf/subscribe [field])
      :on-change #(rf/dispatch [change-handler (target-value %)])}]]])

(defn text-input
  [label field change-handler]
  (input-field label field change-handler :text))

(defn email-input
  [label field change-handler]
  (input-field label field change-handler :email))

(defn password-input
  [label field change-handler]
  (input-field label field change-handler :password))

(defn primary-button
  [label click-handler]
  [:div.field
   [:div.control
    [:a.button.is-primary
     {:on-click #(rf/dispatch [click-handler])}
     label]]])

(defn currency-selector
  []
  (let [currencies @(rf/subscribe [::e.currencies/items])]
    [:div.field
           [:div.control
            [:label.label "Currency"]
            [:div.select
             (->> (for [currency currencies]
                    ^{:key (:name currency)}
                    [:option (:name currency)])
                  (into [:select]))]]]))
