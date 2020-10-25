(ns dinsro.events.transactions
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu :include-macros true]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.events.transactions :as s.e.transactions]
   [dinsro.spec.transactions :as s.transactions]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(def example-transaction
  {:db/id 1
   ::s.transactions/value 130000
   ::s.transactions/date (tick/instant)
   ::s.transactions/currency {:db/id 53}
   ::s.transactions/account {:db/id 12}})

(s/def ::item ::s.transactions/item)

(eu/declare-model 'dinsro.events.transactions)
(eu/declare-fetch-index-method 'dinsro.events.transactions)
(eu/declare-fetch-record-method 'dinsro.events.transactions)
(eu/declare-delete-record-method 'dinsro.events.transactions)

;; Items by Account

(defn items-by-account
  [{:keys [::item-map]} event]
  (let [[_ id] event]
    (filter #(= (get-in % [::s.transactions/account :db/id]) id) (vals item-map))))

(s/fdef items-by-account
  :args (s/cat :db (s/keys :req [::item-map])
               :event ::s.e.transactions/items-by-account-event)
  :ret ::items)

;; Items by Currency

(defn items-by-currency
  [{:keys [::item-map]} [_ id]]
  (filter #(= (get-in % [::s.transactions/currency :db/id]) id) (vals item-map)))

(s/fdef items-by-currency
  :args (s/cat :db (s/keys :req [::item-map])
               :event ::s.e.transactions/items-by-currency-event)
  :ret ::items)

;; Items by User

;; FIXME: This will have to read across all linked accounts
(defn items-by-user
  [{:keys [::item-map]} [_ _user-id]]
  (vals item-map))

;; Submit

(s/def ::do-submit-state ::ds/state)

(defn do-submit-success
  [_store {:keys [db]} _]
  {:db (assoc db ::do-submit-state :loaded)
   :dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_store {:keys [db]} _]
  {:db (assoc db ::do-submit-state :failed)})

(defn do-submit
  [store {:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-transactions]
    store
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-model-store 'dinsro.events.transactions)
    (eu/register-fetch-index-method 'dinsro.events.transactions [:api-index-transactions])
    (eu/register-fetch-record-method 'dinsro.events.transactions [:api-show-transaction])
    (eu/register-delete-record-method 'dinsro.events.transactions [:api-delete-transaction])
    (st/reg-sub ::items-by-account items-by-account)
    (st/reg-sub ::items-by-currency items-by-currency)
    (st/reg-sub ::items-by-user items-by-user)
    (st/reg-basic-sub ::do-submit-state)
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit (partial do-submit store)))
  store)
