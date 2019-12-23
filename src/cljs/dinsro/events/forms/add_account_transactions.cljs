(ns dinsro.events.forms.add-account-transactions
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::s.e.f.add-account-transactions/shown?)
(rfu/reg-set-event ::s.e.f.add-account-transactions/shown?)

(rfu/reg-basic-sub ::s.e.f.add-account-transactions/currency-id)
(rfu/reg-set-event ::s.e.f.add-account-transactions/currency-id)

(rfu/reg-basic-sub ::s.e.f.add-account-transactions/date)
(rfu/reg-set-event ::s.e.f.add-account-transactions/date)

(rfu/reg-basic-sub ::s.e.f.add-account-transactions/value)
(rfu/reg-set-event ::s.e.f.add-account-transactions/value)

(defn form-data-sub
  [[value currency-id date]]
  {:value (.parseFloat js/Number value)
   :currency-id (int currency-id)
   :date        (js/Date. date)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.add-account-transactions/value]
 :<- [::s.e.f.add-account-transactions/currency-id]
 :<- [::s.e.f.add-account-transactions/date]
 form-data-sub)
