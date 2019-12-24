(ns dinsro.events.forms.create-rate
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

;; (rfu/reg-basic-sub ::s.e.f.create-rate/currency-id)
;; (rfu/reg-set-event ::s.e.f.create-rate/currency-id)

;; (rfu/reg-basic-sub ::s.e.f.create-rate/date)
;; (rfu/reg-set-event ::s.e.f.create-rate/date)

;; (rfu/reg-basic-sub ::s.e.f.create-rate/time)
;; (rfu/reg-set-event ::s.e.f.create-rate/time)

;; (rfu/reg-basic-sub ::s.e.f.create-rate/shown?)
;; (rfu/reg-set-event ::s.e.f.create-rate/shown?)

(defn form-data-sub
  [[
    name
    ;; currency-id rate date
    ] _]
  {
   :name name
   ;; :currency-id (int currency-id)
   ;; :rate        (js/Number.parseFloat rate)
   ;; :date        (js/Date. date)

   })

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-rate-source/name]
 form-data-sub)

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)]
    {:db (merge db {
                    ::s.e.f.create-rate-source/name (str default-name)
                    ;; ::s.e.f.create-rate/rate (str default-rate)
                    ;; ::s.e.f.create-rate/currency-id ""
                    ;; ::s.e.f.create-rate/date (.toISOString default-date)
                    })}))

(kf/reg-event-fx ::toggle-form toggle-form)
(kf/reg-event-fx ::init-form init-form)
