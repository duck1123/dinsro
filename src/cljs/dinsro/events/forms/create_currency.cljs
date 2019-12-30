(ns dinsro.events.forms.create-currency
  (:require [dinsro.events.forms.create-currency :as e.f.create-currency]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(def default-name "")

(rfu/reg-basic-sub ::e.f.create-currency/name)
(rfu/reg-set-event ::e.f.create-currency/name)

(rfu/reg-basic-sub ::e.f.create-currency/shown?)
(rfu/reg-set-event ::e.f.create-currency/shown?)

(defn form-data-sub
  [name]
  {:name name})

(rf/reg-sub
 ::form-data
 :<- [::name]
 form-data-sub)

(defn set-defaults
  [{:keys [db]} _]
  {:db (merge db {::name default-name})})

(kf/reg-event-fx ::set-defaults set-defaults)
