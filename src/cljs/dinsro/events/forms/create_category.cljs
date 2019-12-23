(ns dinsro.events.forms.create-category
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.events.categories :as e.categories]
            [dinsro.spec.categories :as s.categories]
            [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::s.e.f.create-category/name)
(rfu/reg-set-event ::s.e.f.create-category/name)

(rfu/reg-basic-sub ::s.e.f.create-category/user-id)
(rfu/reg-set-event ::s.e.f.create-category/user-id)

(rfu/reg-basic-sub ::s.e.f.create-category/shown?)
(rfu/reg-set-event ::s.e.f.create-category/shown?)

(kf/reg-event-db ::toggle-form (fn-traced [db _] (update db ::shown? not)))

(defn form-data-sub
  [[name user-id] _]
  {:name          name
   :user-id       (int user-id)})

(rf/reg-sub
 ::form-data
 :<- [::name]
 :<- [::user-id]
 form-data-sub)
