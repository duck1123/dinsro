(ns dinsro.events.forms.add-currency-rate
  (:require
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.store :as st]))

(defn form-data-sub
  [{:keys [::s.e.f.create-rate/date
           ::s.e.f.create-rate/rate]}
   [_ currency-id]]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        date})

(def form-data ::form-data)

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)
        default-opts {::s.e.f.create-rate/rate (str e.f.create-rate/default-rate)
                      ::s.e.f.create-rate/date (.toISOString default-date)}]
    {:db (merge db default-opts)}))

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::shown?)
    (st/reg-set-event ::shown?)
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::init-form init-form))
  store)
