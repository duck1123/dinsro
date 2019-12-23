(ns dinsro.events.forms.create-account
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-name "Offshore")
(def default-initial-value 1.0)

(s/def ::name string?)
(rfu/reg-basic-sub ::name)
(rfu/reg-set-event ::name)

(s/def ::currency-id string?)
(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(s/def ::user-id string?)
(rfu/reg-basic-sub ::user-id)
(rfu/reg-set-event ::user-id)

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(s/def ::initial-value string?)
(rfu/reg-basic-sub ::initial-value)
(rfu/reg-set-event ::initial-value)

(kf/reg-event-db ::toggle-form (fn-traced [db _] (update db ::shown? not)))

(defn create-form-data
  [[name initial-value currency-id user-id] _]
  {:name          name
   :currency-id   (int currency-id)
   :user-id       (int user-id)
   :initial-value (.parseFloat js/Number initial-value)})

(rf/reg-sub
 ::form-data
 :<- [::name]
 :<- [::initial-value]
 :<- [::currency-id]
 :<- [::user-id]
 create-form-data)

(defn submit-clicked
  [_ _]
  (let [form-data @(rf/subscribe [::account-data])]
    {:dispatch [::e.accounts/do-submit form-data]}))

(kf/reg-event-fx ::submit-clicked submit-clicked)
