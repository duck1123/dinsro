(ns dinsro.events.forms.create-rate-source
  (:require [clojure.spec.alpha :as s]
            [dinsro.events.rate-sources :as e.rate-sources]
            [dinsro.spec.events.forms.create-rate-source :as s.e.f.create-rate-source]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

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
  [[
    name
    url
    currency-id
    ] _]
  {
   :name name
   :url url
   :currency-id (int currency-id)
   ;; :rate        (js/Number.parseFloat rate)
   ;; :date        (js/Date. date)

   })

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-rate-source/name]
 :<- [::s.e.f.create-rate-source/url]
 :<- [::s.e.f.create-rate-source/currency-id]
 form-data-sub)
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
