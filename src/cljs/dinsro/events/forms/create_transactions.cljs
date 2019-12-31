(ns dinsro.events.create-transaction
  (:require [clojure.spec.alpha :as s]
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

(defn form-data-sub
  [[value currency-id date]]
  {:value value
   :currency-id (int currency-id)
   :date        (js/Date. date)})

(rf/reg-sub
 ::form-data
 :<- [::value]
 :<- [::currency-id]
 :<- [::date]
 form-data-sub)
