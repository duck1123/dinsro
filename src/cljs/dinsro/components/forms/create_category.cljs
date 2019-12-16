(ns dinsro.components.forms.create-category
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.categories :as e.categories]
            [dinsro.spec.categories :as s.categories]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(s/def ::name string?)
(rfu/reg-basic-sub ::name)
(rfu/reg-set-event ::name)

(s/def ::user-id string?)
(rfu/reg-basic-sub ::user-id)
(rfu/reg-set-event ::user-id)

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(kf/reg-event-db ::toggle-form (fn-traced [db _] (update db ::shown? not)))

(defn create-form-data
  [[name user-id] _]
  {:name          name
   :user-id       (int user-id)})

(rf/reg-sub
 ::form-data
 :<- [::name]
 :<- [::user-id]
 create-form-data)

(defn new-category-form
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:<>
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::name ::set-name]
       [c/user-selector (tr [:user]) ::user-id ::set-user-id]
       [c/primary-button (tr [:submit]) [::e.categories/do-submit form-data]]])))
