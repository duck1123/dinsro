(ns dinsro.components.forms.create-account
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(s/def ::name string?)
(c/reg-field ::name          "Offshore")
(c/reg-field ::initial-value 1.0)
(c/reg-field ::currency-id   "")
(c/reg-field ::user-id       "")

(s/def ::form-shown? boolean?)
(c/reg-field ::form-shown?   true)

(kf/reg-event-db ::change-currency-id   (fn [db [value]] (assoc db ::currency-id   (int value))))
(kf/reg-event-db ::change-user-id       (fn [db [value]] (assoc db ::user-id       (int value))))
(kf/reg-event-db ::change-name          (fn [db [value]] (assoc db ::name          value)))
(kf/reg-event-db ::change-initial-value (fn [db [value]] (assoc db ::initial-value value)))

(kf/reg-event-db ::toggle-form (fn-traced [db _] (update db ::form-shown? not)))

(defn create-form-data
  [[name initial-value currency-id user-id] _]
  {::s.accounts/name          name
   ::s.accounts/currency-id   (int currency-id)
   ::s.accounts/user-id       (int user-id)
   ::s.accounts/initial-value (.parseFloat js/Number initial-value)})

(rf/reg-sub
 ::form-data
 :<- [::name]
 :<- [::initial-value]
 :<- [::currency-id]
 :<- [::user-id]
 create-form-data)

(defn submit-clicked
  [_ _]
  (let [form-data @(rf/subscribe [::account-data])]
    {:dispatch [::e.accounts/do-submit form-data]}))

(kf/reg-event-fx ::submit-clicked submit-clicked)

(defn new-account-form
  []
  (let [form-data @(rf/subscribe [::form-data])
        shown? @(rf/subscribe [::form-shown?])]
    [:<>
     [:a.button {:on-click #(rf/dispatch [::toggle-form])} (tr [:toggle])]
     #_[:pre (str form-data)]
     (when shown?
       [:<>
        [c/text-input (tr [:name]) ::name ::change-name]
        [c/number-input (tr [:initial-value]) ::initial-value ::change-initial-value]
        [c/currency-selector (tr [:currency]) ::currency-id ::change-currency-id]
        [c/user-selector (tr [:user]) ::user-id ::change-user-id]
        [c/primary-button (tr [:submit]) [::e.accounts/do-submit form-data]]])]))
