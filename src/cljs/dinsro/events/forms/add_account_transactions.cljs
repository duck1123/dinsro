(ns dinsro.events.forms.add-account-transactions)

(rfu/reg-basic-sub ::s.e.f.add-account-transactions/shown?)
(rfu/reg-set-event ::s.e.f.add-account-transactions/shown?)

(rfu/reg-basic-sub ::s.e.f.add-account-transactions/currency-id)
(rfu/reg-set-event ::s.e.f.add-account-transactions/currency-id)

(rfu/reg-basic-sub ::s.e.f.add-account-transactions/date)
(rfu/reg-set-event ::s.e.f.add-account-transactions/date)

(rfu/reg-basic-sub ::s.e.f.add-account-transactions/value)
(rfu/reg-set-event ::s.e.f.add-account-transactions/value)

(defn toggle
  [cofx event]
  (let [{:keys [db]} cofx]
    {:db (update db ::s.e.f.add-account-transactions/shown? not)}))

(kf/reg-event-fx ::s.e.f.add-account-transactions/toggle toggle)

(defn toggle-button
  []
  (let [shown? @(rf/subscribe [::s.e.f.add-account-transactions/shown?])]
    [:a {:on-click #(rf/dispatch [::s.e.f.add-account-transactions/toggle])}
     (if shown?
       [:span.icon>i.fas.fa-chevron-down]
       [:span.icon>i.fas.fa-chevron-right])]))

(defn create-form-data
  [[value currency-id date]]
  {:value (.parseFloat js/Number value)
   :currency-id (int currency-id)
   :date        (js/Date. date)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.add-account-transactions/value]
 :<- [::s.e.f.add-account-transactions/currency-id]
 :<- [::s.e.f.add-account-transactions/date]
 create-form-data)
