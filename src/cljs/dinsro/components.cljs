(ns dinsro.components
  (:require
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.users :as e.users]
   [dinsro.spec.rate-sources :as s.rate-sources]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
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
  [store label field change-handler type]
  [:<>
   [:label.label label]
   [:input.input
    {:type type
     :name (name field)
     :value @(st/subscribe store [field])
     :on-change #(st/dispatch store [change-handler (target-value %)])}]])

(defn checkbox-input
  [store label field change-handler]
  (let [checked (st/subscribe store [field])]
    [:label.checkbox
     [:input {:type :checkbox
              :on-change #(st/dispatch store [change-handler (not @checked)])
              :checked @checked}]
     label]))

(defn text-input
  ([store label field]
   (text-input store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (input-field store label field change-handler :text)))

(defn email-input
  ([store label field]
   (email-input store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (input-field store label field change-handler :email)))

(defn password-input
  ([store label field]
   (password-input store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (input-field store label field change-handler :password)))

(defn number-input
  ([store label field]
   (number-input store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (input-field store label field change-handler :number)))

(defn primary-button
  [store label click-handler]
  [:a.button.is-primary {:on-click #(st/dispatch store click-handler)} label])

(defn account-selector
  ([store label field]
   (account-selector store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (condp = @(st/subscribe store [::e.accounts/do-fetch-index-state])
     :invalid
     [:p "Invalid"]

     :loaded
     (let [items @(st/subscribe store [::e.accounts/items])]
       (comment [:label.label label])
       [:div.select
        (into [:select {:value (or @(st/subscribe store [field]) "")
                        :on-change #(st/dispatch store [change-handler (target-value %)])}]
              (concat [[:option {:value ""} ""]]
                      (for [{:keys [db/id dinsro.spec.accounts/name]} items]
                        ^{:key id} [:option {:value id} name])))])

     [:p "Unknown state"])))

(defn currency-selector-loaded
  [store label field change-handler]
  (let [currencies @(st/subscribe store [::e.currencies/items])]
    [:<>
     [:label.label label]
     [:div.control
      [:div.select
       (into [:select {:value (or @(st/subscribe store [field]) "")
                       :on-change #(st/dispatch store [change-handler (target-value %)])}]
             (concat [[:option {:value ""} "sats"]]
                     (for [{:keys [db/id dinsro.spec.currencies/name]} currencies]
                       ^{:key id} [:option {:value id} name])))]]]))

(defn currency-selector
  ([store label field]
   (currency-selector store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (let [state @(st/subscribe store [::e.currencies/do-fetch-index-state])]
     (condp = state
       :invalid
       [:p "Invalid"]

       :loaded
       [currency-selector-loaded store label field change-handler]

       [:p "Unknown state"]))))

(defn user-selector-
  [store label field change-handler items]
  (let [value (or @(st/subscribe store [field]) "")]
    [:div.field
     [:label.label label]
     [:div.control
      [:div.select
       (into
        [:select {:value value
                  :on-change #(st/dispatch store [change-handler (target-value %)])}]
        (concat [[:option {:value ""} ""]]
                (for [{:keys [db/id dinsro.spec.users/name]} items]
                  ^{:key id}
                  [:option {:value id} name])))]]]))

(defn user-selector
  ([store label field]
   (user-selector store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (let [items @(st/subscribe store [::e.users/items])
         state @(st/subscribe store [::e.users/do-fetch-index-state])]
     (condp = state
       :invalid [:p "Invalid"]
       :loaded  [user-selector- store label field change-handler items]
       [:p "Unknown"]))))

(defn rate-source-selector-
  [store label field change-handler items]
  (let [value (or @(st/subscribe store [field]) "")]
    [:div.field
     [:label.label label]
     [:div.control
      [:div.select
       (into
        [:select {:value value
                  :on-change #(st/dispatch store [change-handler (target-value %)])}]
        (for [source items]
          ^{:key (:db/id source)}
          [:option (str (::s.rate-sources/name source))]))]]]))

(defn rate-source-selector
  ([store label field]
   (rate-source-selector store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (let [items @(st/subscribe store [::e.rate-sources/items])
         state @(st/subscribe store [::e.rate-sources/do-fetch-index-state])]
     (condp = state
       :invalid [:p "Invalid"]
       :loaded [rate-source-selector- store label field change-handler items]
       [:p "Unknown"]))))

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
  [store key]
  [:a.delete.is-pulled-right
   {:on-click #(st/dispatch store [key false])}])

(defn show-form-button
  ([store state]
   (show-form-button store state (#'rfu/kw-prefix state "set-")))
  ([store state change]
   (when-not @(st/subscribe store [state])
     [:a.is-pulled-right {:on-click #(st/dispatch store [change true])}
      (tr [:show-form "Show"])])))

(defn error-message-box
  [message]
  (when (seq message)
    [:div.message.is-danger
     [:div.message-header
      [:p "Error"]]
     [:div.message-body
      message]]))
