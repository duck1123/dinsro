(ns dinsro.components.forms.create-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-name "Foo")

(s/def ::name string?)
(rfu/reg-basic-sub ::name)
(rfu/reg-set-event ::name)

(defn create-form-data
  [name]
  {:name name})

(rf/reg-sub
 ::form-data
 :<- [::name]
 create-form-data)

(defn submit-clicked
  [_ _]
  (let [form-data @(rf/subscribe [::form-data])]
    {:dispatch [::e.currencies/do-submit form-data]}))

(kf/reg-event-fx ::submit-clicked submit-clicked)

(defn debug-box
  [form-data]
  (when @(rf/subscribe [::debug-shown?])
    [:pre (str form-data)]))

(defn create-currency
  []
  (let [form-data @(rf/subscribe [::form-data])]
    [:<>
     [c.debug/debug-box form-data]
     [:form
      [c/text-input     "Name"   ::name ::set-name]
      [c/primary-button "Submit" [::submit-clicked]]]]))

(defn init-form
  [{:keys [db]} _]
  {:db (merge db {::name default-name})})

(kf/reg-event-fx ::init-form init-form)

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::init-form]})
