(ns dinsro.events.forms.add-user-account
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.utils :as eu]
   [dinsro.specs.actions.accounts :as s.a.accounts]
   [dinsro.specs.events.forms.create-account :as s.e.f.create-account]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.add-user-account)

(eu/declare-subform
 ns-sym
 ::s.a.accounts/create-params-valid
 [[:currency-id ::s.e.f.create-account/currency-id 0]
  [:initial-value ::s.e.f.create-account/initial-value 0]
  [:name ::s.e.f.create-account/name ""]
  [:user-id ::s.e.f.create-account/user-id 0]])

(def default-name "Offshore")

(s/def ::form-data-db (s/keys :req [::s.e.f.create-account/currency-id
                                    ::s.e.f.create-account/initial-value
                                    ::s.e.f.create-account/name
                                    ::s.e.f.create-account/user-id]))
(s/def ::form-data-event (s/cat :kw keyword?))
(s/def ::form-data (s/keys))

(defn form-data-sub
  [{:keys [::s.e.f.create-account/currency-id
           ::s.e.f.create-account/initial-value
           ::s.e.f.create-account/name
           ::s.e.f.create-account/user-id]}
   _]
  (merge
   (when (not= currency-id "")
     {:currency-id   (int currency-id)})
   {:name          name
    :user-id       (int user-id)
    :initial-value (.parseFloat js/Number initial-value)}))

(s/fdef form-data-sub
  :args (s/cat :db ::form-data-db
               :event ::form-data-event)
  :ret ::form-data)

(defn init-handlers!
  [store]
  (doto store
    (eu/register-subform ns-sym)
    (st/reg-sub ::form-data form-data-sub))
  store)
