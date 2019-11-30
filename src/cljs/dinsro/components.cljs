(ns dinsro.components
  (:require [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(def strings {})

(defn l
  [keyword]
  (get strings keyword (str "Missing string: " keyword)))

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
     {:on-click #(rf/dispatch click-handler)}
     label]]])

(defn currency-selector
  [label field change-handler]
  (let [state @(rf/subscribe [::e.currencies/do-fetch-index-state])]
    [:<>
     #_[:a.button {:on-click #(rf/dispatch [::e.currencies/do-fetch-index])}
      (str "Fetch Currencies: " state)]
     (condp = state
       :invalid
       [:p "Invalid"]

       :loaded
       (let [currencies @(rf/subscribe [::e.currencies/items])]
         [:div.field>div.control
          [:label.label label]
          [:div.select
           (into [:select {:value @(rf/subscribe [field])
                           :on-change #(rf/dispatch [change-handler (target-value %)])}]
                 (for [{:keys [db/id dinsro.spec.currencies/name]} currencies]
                   ^{:key id} [:option {:value id} name]))]])

       [:p "Unknown state"])]))

(defn user-selector-
  [label field change-handler items]
  [:div.field>div.control
   [:label.label label]
   [:div.select
    (into [:select {:value @(rf/subscribe [field])
                    :on-change #(rf/dispatch [change-handler (target-value %)])}]
          (for [{:keys [db/id dinsro.spec.users/name]} items]
            ^{:key id}
            [:option {:value id} name]))]])

(defn user-selector
  [label field change-handler]
  (let [items @(rf/subscribe [::e.users/items])
        state @(rf/subscribe [::e.users/do-fetch-index-state])]
    [:<>
     #_[:a.button {:on-click #(rf/dispatch [::e.users/do-fetch-index])} (str "Fetch Users: " state)]
     (condp = state
       :invalid [:p "Invalid"]
       :loaded  [user-selector- label field change-handler items]
       [:p "Unknown"])]))

(defn filter-page
  [page]
  #(when (= (get-in % [:data :name]) page) true))

(defn filter-param-page
  [page]
  (fn [match]
    (when (= (get-in match [:data :name]) page)
      (:path-params match))))
