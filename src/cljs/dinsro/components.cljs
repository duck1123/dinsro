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

(defn number-input
  [label field change-handler]
  (input-field label field change-handler :number))

(defn primary-button
  [label click-handler]
  [:div.field
   [:div.control
    [:a.button.is-primary
     {:on-click #(rf/dispatch [click-handler])}
     label]]])

(defn currency-selector
  [label field change-handler]
  (let [currencies @(rf/subscribe [::e.currencies/items])]
    [:div.field>div.control
     [:label.label label]
     [:div.select
      (into [:select {:value @(rf/subscribe [field])
                      :on-change #(rf/dispatch [change-handler (target-value %)])}]
            (for [{:keys [db/id dinsro.model.currencies/name]} currencies]
              ^{:key name}
              [:option {:value id} name]))]]))
