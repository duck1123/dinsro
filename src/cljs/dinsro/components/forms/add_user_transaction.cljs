(ns dinsro.components.forms.add-user-transaction
  (:require
   [dinsro.components :as c]
   [dinsro.components.datepicker :as c.datepicker]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.translations :refer [tr]]
   [re-frame.core :as rf]))

(defn error-message
  [message]
  [:div.message.is-danger
   [:div.message-header
    [:p "Error"]]
   [:div.message-body
    message]])

(defn form-shown
  [form-data]
  (let [state (rf/subscribe [::e.transactions/do-submit-state])]
    [:div
     [c/close-button ::e.f.add-user-transaction/set-shown?]
     [c.debug/debug-box form-data]
     (when (= @state :failed)
       [error-message "There was an error submitting"])
     [:p (str @state)]
     [:div.field>div.control
      [c/number-input (tr [:value]) ::s.e.f.create-transaction/value]]
     [:div.field>div.control
      [:label.label>div.control (tr [:account])]
      [c/account-selector (tr [:account]) ::s.e.f.create-transaction/account-id]]
     [:div.field>div.control
      [:label.label (tr [:date])]
      [c.datepicker/datepicker {:on-select #(rf/dispatch [::s.e.f.create-transaction/set-date %])}]]
     [:div.field>div.control
      [c/primary-button (tr [:submit]) [::e.transactions/do-submit form-data]]]]))

(defn form
  []
  ;; TODO: get account id
  (let [form-data @(rf/subscribe [::e.f.add-user-transaction/form-data 1])]
    (when @(rf/subscribe [::e.f.add-user-transaction/shown?])
      [form-shown form-data])))
