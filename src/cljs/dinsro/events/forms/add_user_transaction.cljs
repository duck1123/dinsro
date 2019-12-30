(ns dinsro.events.forms.add-user-transaction
  (:require [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(rfu/reg-basic-sub ::date)
(rfu/reg-set-event ::date)

(rfu/reg-basic-sub ::value)
(rfu/reg-set-event ::value)

(defn-spec form-data-sub ::form-data-output
  [[value currency-id] ::form-data-input
   _ any?]
  {:value value
   :currency-id (int currency-id)})

(rf/reg-sub
 ::form-data-sub
 :<- [::value]
 :<- [::currency-id]
 create-form-data)
