(ns dinsro.events.forms.add-user-transaction
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.forms.create-category :as c.f.create-category]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.actions.transactions :as s.a.transactions]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
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

(defn-spec create-form-data ::form-data-output
  [[value currency-id] ::form-data-input
   _ any?]
  {:value value
   :currency-id (int currency-id)})

(comment
  (gen/generate (s/gen ::form-data-input))
  (gen/generate (s/gen ::form-data-output))
  (create-form-data ["1" "1"] [])
  )

(rf/reg-sub
 ::form-data
 :<- [::value]
 :<- [::currency-id]
 create-form-data)
