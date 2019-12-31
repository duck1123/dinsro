(ns dinsro.components
  (:require [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
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
  [:<>
   [:label.label label]
   [:input.input
    {:type type
     :value @(rf/subscribe [field])
     :on-change #(rf/dispatch [change-handler (target-value %)])}]])


(defn checkbox-input
  [label field change-handler]
  (let [checked (rf/subscribe [field])]
    [:label.checkbox
     [:input {:type :checkbox
              :on-change #(rf/dispatch [change-handler (not @checked)])
              :checked @checked}]
     label]))

(defn text-input
  ([label field]
   (text-input label field (#'rfu/kw-prefix field "set-")))
  ([label field change-handler]
   (input-field label field change-handler :text)))

(defn email-input
  ([label field]
   (email-input label field (#'rfu/kw-prefix field "set-")))
  ([label field change-handler]
   (input-field label field change-handler :email)))

(defn password-input
  ([label field]
   (password-input label field (#'rfu/kw-prefix field "set-")))
  ([label field change-handler]
   (input-field label field change-handler :password)))

(defn number-input
  ([label field]
   (number-input label field (#'rfu/kw-prefix field "set-")))
  ([label field change-handler]
   (input-field label field change-handler :number)))

(defn primary-button
  [label click-handler]
  [:a.button.is-primary {:on-click #(rf/dispatch click-handler)} label])

(defn account-selector
  ([label field]
   (account-selector label field (#'rfu/kw-prefix field "set-")))
  ([label field change-handler]
   (condp = @(rf/subscribe [::e.accounts/do-fetch-index-state])
     :invalid
     [:p "Invalid"]

     :loaded
     (let [items @(rf/subscribe [::e.accounts/items])]
       #_[:label.label label]
       [:div.select
        (into [:select {:value (or @(rf/subscribe [field]) "")
                        :on-change #(rf/dispatch [change-handler (target-value %)])}]
              (concat [[:option {:value ""} ""]]
                      (for [{:keys [db/id dinsro.spec.accounts/name]} items]
                        ^{:key id} [:option {:value id} name])))])

     [:p "Unknown state"])))

(defn currency-selector-loaded
  [label field change-handler]
  (let [currencies @(rf/subscribe [::e.currencies/items])]
    [:<>
     [:label.label label]
     [:div.control
      [:div.select
       (into [:select {:value (or @(rf/subscribe [field]) "")
                       :on-change #(rf/dispatch [change-handler (target-value %)])}]
             (concat [[:option {:value ""} ""]]
                     (for [{:keys [db/id dinsro.spec.currencies/name]} currencies]
                       ^{:key id} [:option {:value id} name])))]]]))

(defn currency-selector
  ([label field]
   (currency-selector label field (#'rfu/kw-prefix field "set-")))
  ([label field change-handler]
   (let [state @(rf/subscribe [::e.currencies/do-fetch-index-state])]
     (condp = state
       :invalid
       [:p "Invalid"]

       :loaded
       [currency-selector-loaded label field change-handler]

       [:p "Unknown state"]))))

(defn user-selector-
  [label field change-handler items]
  (let [value (or @(rf/subscribe [field]) "")]
    [:div.field>div.control
     [:label.label label]
     [:div.select
      (into [:select {:value value
                      :on-change #(rf/dispatch [change-handler (target-value %)])}]
            (concat [[:option {:value ""} ""]]
                    (for [{:keys [db/id dinsro.spec.users/name]} items]
                      ^{:key id}
                      [:option {:value id} name])))]]))

(defn user-selector
  ([label field]
   (user-selector label field (#'rfu/kw-prefix field "set-")))
  ([label field change-handler]
   (let [items @(rf/subscribe [::e.users/items])
         state @(rf/subscribe [::e.users/do-fetch-index-state])]
     [:<>
      #_[:a.button {:on-click #(rf/dispatch [::e.users/do-fetch-index])} (str "Fetch Users: " state)]
      (condp = state
        :invalid [:p "Invalid"]
        :loaded  [user-selector- label field change-handler items]
        [:p "Unknown"])])))

(defn filter-page
  [page]
  #(when (= (get-in % [:data :name]) page) true))

(defn filter-param-page
  [page]
  (fn [match]
    (when (= (get-in match [:data :name]) page)
      (:path-params match))))

(defn get-date-string
  [date]
  (str (.getFullYear date) "-" (inc (.getMonth date)) "-0" (.getDate date)))

(defn get-time-string
  [date]
  (str (.getHours date) ":" (.getMinutes date)))

(defn close-button
  [key]
  [:a.delete.is-pulled-right
   {:on-click #(rf/dispatch [key false])}])

(defn show-form-button
  ([state]
   (show-form-button state (#'rfu/kw-prefix state "set-")))
  ([state change]
   (when-not @(rf/subscribe [state])
     [:a.is-pulled-right {:on-click #(rf/dispatch [change true])}
      (tr [:show-form "Show"])])))
