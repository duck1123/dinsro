(ns dinsro.events.accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu :include-macros true]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.events.accounts :as s.e.accounts]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(s/def ::item ::s.accounts/item)

(eu/declare-model 'dinsro.events.accounts)
(eu/declare-fetch-index-method 'dinsro.events.accounts)
(eu/declare-fetch-record-method 'dinsro.events.accounts)

;; Items by User

(defn items-by-user
  [{:keys [::item-map]} [_ id]]
  (filter #(= id (get-in % [::s.accounts/user :db/id])) (vals item-map)))

(s/fdef items-by-user
  :args (s/cat :db (s/keys :req [::item-map])
               :event (s/cat :kw keyword?
                             :id :db/id))
  :ret ::items)

;; Items by Currency

(defn items-by-currency
  [{:keys [::item-map]} [_ item]]
  (let [id (:db/id item)]
    (filter #(= id (get-in % [::s.accounts/currency :db/id])) (vals item-map))))

(s/fdef items-by-currency
  :args (s/cat :db (s/keys :req [::item-map])
               :event any?)
  :ret ::items)

;; Create

(s/def ::do-submit-state ::ds/state)

(defn do-submit-success
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_store _cofx _event]
  {})

(defn do-submit
  [store {:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-accounts]
    store
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

(s/fdef do-submit
  :args (s/cat :cofx ::s.e.accounts/do-submit-response-cofx
               :event ::s.e.accounts/do-submit-response-event)
  :ret ::s.e.accounts/do-submit-response)

(defn init-handlers!
  [store]
  (doto store
    (eu/register-model-store 'dinsro.events.accounts)
    (eu/register-fetch-index-method 'dinsro.events.accounts [:api-index-accounts])
    (eu/register-fetch-record-method 'dinsro.events.accounts [:api-show-account])
    (eu/register-delete-record-method 'dinsro.events.accounts [:api-show-account])
    (st/reg-sub ::items-by-currency items-by-currency)
    (st/reg-sub ::items-by-user items-by-user)
    (st/reg-basic-sub ::do-submit-state)
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit (partial do-submit store)))
  store)
