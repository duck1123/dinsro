(ns dinsro.components.forms.account
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.accounts :as s.accounts]
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

(rf/reg-sub
 ::form-data
 :<- [::name]
 :<- [::initial-value]
 :<- [::currency-id]
 :<- [::user-id]
 (fn-traced [[name initial-value currency-id user-id] _]
   {::s.accounts/name          name
    ::s.accounts/currency-id   currency-id
    ::s.accounts/user-id       user-id
    ::s.accounts/initial-value (.parseFloat js/Number initial-value)}))

(kf/reg-event-fx
 ::submit-clicked
 (fn-traced [_ _]
  {:dispatch [::e.accounts/do-submit @(rf/subscribe [::account-data])]}))

(def strings
  {:name     "Name"
   :initial-value "Initial Value"
   :currency "Currency"
   :submit "Submit"
   :user "User"})

(def l strings)

(defn create-user-account
  []
  (let [account-data @(rf/subscribe [::account-data])]
    [:div
     [:p "Create User Account"]
     [:pre (str account-data)]
     [:form.form
      [c/text-input        (l :name)          ::name          ::change-name]
      [c/number-input      (l :initial-value) ::initial-value ::change-initial-value]
      [c/currency-selector (l :currency)      ::currency-id   ::change-currency-id]
      #_[c/user-selector     "User"          ::user-id       ::change-user-id]
      [c/primary-button    (l :submit)        [::submit-clicked account-data]]]]))

(defn new-account-form
  []
  (let [form-data @(rf/subscribe [::form-data])
        shown? @(rf/subscribe [::form-shown?])]
    [:<>
     [:a.button {:on-click #(rf/dispatch [::toggle-form])} "Toggle"]
     [:div.box
      [:pre (str form-data)]
      (when shown?
        [:form.form
         [c/text-input        (:name strings)          ::name          ::change-name]
         [c/number-input      (:initial-value strings) ::initial-value ::change-initial-value]
         [c/currency-selector (:currency strings)      ::currency-id   ::change-currency-id]
         [c/user-selector     (:user strings)          ::user-id       ::change-user-id]
         [c/primary-button    (:submit strings)        [::submit-clicked form-data]]])]]))
