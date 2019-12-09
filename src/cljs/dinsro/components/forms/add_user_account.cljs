(ns dinsro.components.forms.add-user-account
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.users :as s.users]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(s/def ::shown? boolean?)
(c/reg-field ::shown?   true)

(defn toggle
  [cofx event]
  (let [{:keys [db]} cofx]
    {:db (update db ::shown? not)}))

(kf/reg-event-fx ::toggle toggle)

(s/def ::name string?)
(c/reg-field ::name          "Offshore")
(c/reg-field ::initial-value 1.0)
(c/reg-field ::currency-id   "")
(c/reg-field ::user-id       "")
(kf/reg-event-db ::change-currency-id   (fn [db [value]] (assoc db ::currency-id   (int value))))
(kf/reg-event-db ::change-user-id       (fn [db [value]] (assoc db ::user-id       (int value))))
(kf/reg-event-db ::change-name          (fn [db [value]] (assoc db ::name          value)))
(kf/reg-event-db ::change-initial-value (fn [db [value]] (assoc db ::initial-value value)))

(rf/reg-sub
 ::form-data
 :<- [::name]
 :<- [::initial-value]
 :<- [::currency-id]
 :<- [::user-id]
 (fn [[name initial-value currency-id user-id] _]
   {::s.accounts/name          name
    ::s.accounts/currency-id   currency-id
    ::s.accounts/user-id       user-id
    ::s.accounts/initial-value (.parseFloat js/Number initial-value)}))

(def strings
  {:name     "Name"
   :initial-value "Initial Value"
   :currency "Currency"
   :submit "Submit"
   :user "User"})

(def l strings)

(defn add-user-account
  [user-id]
  (let [shown? @(rf/subscribe [::shown?])
        form-data (assoc @(rf/subscribe [::form-data]) ::s.accounts/user-id user-id)]
    [:<>
     [:a {:style {:margin-left "5px"}
          :on-click #(rf/dispatch [::toggle])}
      (if shown?
        [:span.icon>i.fas.fa-chevron-down]
        [:span.icon>i.fas.fa-chevron-right])]
     (when shown?
       [:<>
        [c/text-input        (l :name)          ::name          ::change-name]
        [c/number-input      (l :initial-value) ::initial-value ::change-initial-value]
        [c/currency-selector (l :currency)      ::currency-id   ::change-currency-id]
        [c/primary-button    (l :submit)        [::e.accounts/do-submit form-data]]])]))
