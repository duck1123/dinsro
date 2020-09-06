(ns dinsro.events.forms.add-user-account
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(def default-name "Offshore")

(defn form-data-sub
  [db _]
  (let [{:keys [::s.e.f.create-account/currency-id
                ::s.e.f.create-account/initial-value
                ::s.e.f.create-account/name
                ::s.e.f.create-account/user-id]} db]
    (merge
     (when (not= currency-id "")
       {:currency-id   (int currency-id)})
     {:name          name
      :user-id       (int user-id)
      :initial-value (.parseFloat js/Number initial-value)})))

(s/fdef form-data-sub
  :args (s/cat :cofx (s/keys)
               :event any?)
  :ret (s/keys))

(rf/reg-sub ::form-data form-data-sub)
(def form-data ::form-data)
