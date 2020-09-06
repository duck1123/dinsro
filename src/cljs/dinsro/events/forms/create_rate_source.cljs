(ns dinsro.events.forms.create-rate-source
  (:require
   [dinsro.spec.events.forms.create-rate-source :as s.e.f.create-rate-source]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]))

(def default-name "Default Source")

(rfu/reg-basic-sub ::s.e.f.create-rate-source/name)
(rfu/reg-set-event ::s.e.f.create-rate-source/name)

(rfu/reg-basic-sub ::s.e.f.create-rate-source/url)
(rfu/reg-set-event ::s.e.f.create-rate-source/url)

(rfu/reg-basic-sub ::s.e.f.create-rate-source/currency-id)
(rfu/reg-set-event ::s.e.f.create-rate-source/currency-id)

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn form-data-sub
  [{:keys [::s.e.f.create-rate-source/currency-id
           ::s.e.f.create-rate-source/name
           ::s.e.f.create-rate-source/url]}

   _]
  {:name name
   :url url
   :currency-id (int currency-id)})

(rf/reg-sub ::form-data form-data-sub)
(def form-data ::form-data)

(defn init-form
  [{:keys [db]} _]
  {:db (merge db {
                  ::s.e.f.create-rate-source/name (str s.e.f.create-rate-source/default-name)
                  ::s.e.f.create-rate-source/url s.e.f.create-rate-source/default-url
                  ::s.e.f.create-rate-source/currency-id (str s.e.f.create-rate-source/default-currency-id)
                  })})

;; (kf/reg-event-fx ::toggle-form toggle-form)
(kf/reg-event-fx ::init-form init-form)
