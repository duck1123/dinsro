(ns dinsro.ui.inputs
  (:require
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.users :as e.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.users :as m.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(def target-value #(-> % .-target .-value))

(defn reg-field
  [store key default]
  (st/reg-sub store key (fn [db _] (get db key default))))

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
  ([store _label field change-handler]
   (condp = @(st/subscribe store [::e.accounts/do-fetch-index-state])
     :invalid
     [:p "Invalid Account Fetch State"]

     :loaded
     (let [items @(st/subscribe store [::e.accounts/items])]
       [:div.select
        (into [:select {:value (or @(st/subscribe store [field]) "")
                        :on-change #(st/dispatch store [change-handler (target-value %)])}]
              (concat [[:option {:value ""} ""]]
                      (for [{:db/keys [id] ::m.accounts/keys [name]} items]
                        ^{:key id} [:option {:value id} name])))])

     [:p "Unknown Account Fetch state"])))

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
                     (for [{:db/keys [id] ::m.currencies/keys [name]} currencies]
                       ^{:key id} [:option {:value id} name])))]]]))

(defn currency-selector
  ([store label field]
   (currency-selector store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (let [state @(st/subscribe store [::e.currencies/do-fetch-index-state])]
     (condp = state
       :invalid
       [:p "Invalid Currency Fetch State"]

       :loaded
       [currency-selector-loaded store label field change-handler]

       [:p "Unknown Currency fetch state"]))))

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
                (for [{::m.users/keys [name]
                       :db/keys [id]} items]
                  ^{:key id}
                  [:option {:value id} name])))]]]))

(defn user-selector
  ([store label field]
   (user-selector store label field (#'rfu/kw-prefix field "set-")))
  ([store label field change-handler]
   (let [items @(st/subscribe store [::e.users/items])
         state @(st/subscribe store [::e.users/do-fetch-index-state])]
     (condp = state
       :invalid [:p "Invalid User fetch state"]
       :loaded  [user-selector- store label field change-handler items]
       [:p "Unknown user fetch state"]))))

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
          [:option (str (::m.rate-sources/name source))]))]]]))

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
