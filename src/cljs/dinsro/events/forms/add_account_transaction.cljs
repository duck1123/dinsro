(ns dinsro.events.forms.add-account-transaction
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]))

(s/def ::shown? boolean?)
(def shown? ::shown?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn form-data-sub
  [db event]
  (let [[_ account-id] event
        updated-db (-> db
                       (select-keys [::s.e.f.create-transaction/date
                                     ::s.e.f.create-transaction/description
                                     ::s.e.f.create-transaction/value])
                       (assoc ::s.e.f.create-transaction/account-id account-id))]
    (e.f.create-transaction/form-data-sub updated-db event)))

(s/def ::form-data (s/keys))
(s/def ::form-data-db (s/keys :req [::s.e.f.create-transaction/date
                                    ::s.e.f.create-transaction/description
                                    ::s.e.f.create-transaction/value]))
(s/def ::form-data-event (s/cat :kw keyword?
                                :account-id ::s.e.f.create-transaction/account-id))

(s/fdef form-data-sub
  :args (s/cat :db ::form-data-db
               :event ::form-data-event)
  :ret ::form-data)

(rf/reg-sub ::form-data form-data-sub)
(def form-data ::form-data)
