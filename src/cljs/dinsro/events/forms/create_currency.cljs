(ns dinsro.events.forms.create-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
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

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn create-form-data
  [name]
  {:name name})

(rf/reg-sub
 ::form-data
 :<- [::name]
 create-form-data)

(defn set-defaults
  [{:keys [db]} _]
  {:db (merge db {::name default-name})})

(kf/reg-event-fx ::set-defaults set-defaults)
