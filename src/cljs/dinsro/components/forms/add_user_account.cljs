(ns dinsro.components.forms.add-user-account
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn toggle
  [cofx event]
  (let [{:keys [db]} cofx]
    {:db (update db ::shown? not)}))

(kf/reg-event-fx ::toggle toggle)

(def default-name "Offshore")

(s/def ::name string?)
(rfu/reg-basic-sub ::name)
(rfu/reg-set-event ::name)

(s/def ::initial-value number?)
(rfu/reg-basic-sub ::initial-value)
(rfu/reg-set-event ::initial-value)

(s/def ::currency-id string?)
(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(s/def ::user-id string?)
(rfu/reg-basic-sub ::user-id)
(rfu/reg-set-event ::user-id)

(s/def ::create-handler-request (s/keys :req-un [::s.accounts/name]))
(s/def ::form-bindings (s/cat
                        :name ::name
                        :initial-value ::initial-value
                        :currency-id ::currency-id
                        :user-id ::user-id))

(defn-spec create-form-data (s/keys)
  [[name initial-value currency-id user-id] ::form-bindings _ any?]
  {:name          name
   :currency-id   (int currency-id)
   :user-id       (int user-id)
   :initial-value (.parseFloat js/Number initial-value)})

(comment
  (gen/generate (s/gen ::form-bindings))
  (create-form-data ["Bob" "1" "1" "1"])
  (create-form-data (gen/generate (s/gen ::form-bindings)))
  )

(rf/reg-sub
 ::form-data
 :<- [::name]
 :<- [::initial-value]
 :<- [::currency-id]
 :<- [::user-id]
 create-form-data)

(defn add-user-account
  [user-id]
  (let [form-data (assoc @(rf/subscribe [::form-data]) :user-id user-id)]
    (when @(rf/subscribe [::shown?])
      [:<>
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::name ::set-name]
       [c/number-input (tr [:initial-value]) ::initial-value ::set-initial-value]
       [c/currency-selector (tr [:currency]) ::currency-id ::set-currency-id]
       [c/primary-button (tr [:submit]) [::e.accounts/do-submit form-data]]])))
