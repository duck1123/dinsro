(ns dinsro.events.create-transaction
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(s/def ::currency-id string?)
(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(s/def ::date string?)
(rfu/reg-basic-sub ::date)
(rfu/reg-set-event ::date)

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(s/def ::value string?)
(rfu/reg-basic-sub ::value)
(rfu/reg-set-event ::value)

(defn create-form-data
  [[value currency-id date]]
  {:value value
   :currency-id (int currency-id)
   :date        (js/Date. date)})

(rf/reg-sub
 ::form-data
 :<- [::value]
 :<- [::currency-id]
 :<- [::date]
 create-form-data)

(defn toggle-form
  [db _]
  (update db ::shown? not))

(kf/reg-event-db ::toggle-form toggle-form)
