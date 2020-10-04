(ns dinsro.events.transactions
  (:require
   [clojure.spec.alpha :as s]
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

;; Item Map

(s/def ::item-map (s/map-of ::ds/id ::s.transactions/item))
(def item-map ::item-map)

;; Items

(s/def ::items (s/coll-of ::item))

(defn items-sub
  "Subscription handler: Index all items"
  [{:keys [::item-map]} _]
  (sort-by :db/id (vals item-map)))

(s/fdef items-sub
  :args (s/cat :db (s/keys :req [::item-map])
               :event (s/cat :kw keyword?))
  :ret ::items)

;; Item

(defn item-sub
  "Subscription handler: Lookup an item from the item map by id"
  [{:keys [::item-map]} [_ id]]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :db (s/keys :req [::item-map])
               :event (s/cat :kw keyword? :id :db/id))
  :ret ::item)

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

;; Index

(s/def ::do-fetch-index-state keyword?)

(defn do-fetch-index-success
  [_store {:keys [db]} [{:keys [items]}]]
  (let [items (map
               (fn [item] (update item ::s.transactions/date tick/instant))
               items)]
    {:db (-> db
             (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
             (assoc ::do-fetch-index-state :loaded))}))

(defn do-fetch-index-failed
  [_store {:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn do-fetch-index
  [store {:keys [db]} _]
  {:http-xhrio
   (e/fetch-request-auth
    [:api-index-transactions]
    store
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(s/fdef do-fetch-index
  :args (s/cat :cofx ::s.e.transactions/do-fetch-index-cofx
               :event ::s.e.transactions/do-fetch-index-event)
  :ret ::s.e.transactions/do-fetch-index-response)

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

;; Delete

(defn do-delete-record-success
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record
  [store {:keys [db]} [item]]
  (let [id (:db/id item)]
    {:http-xhrio
     (e/delete-request-auth
      [:api-show-transaction {:id id}]
      store
      (:token db)
      [::do-delete-record-success]
      [::do-delete-record-failed])}))

(s/fdef do-delete-record
  :args (s/cat :cofx ::s.e.transactions/do-delete-record-cofx
               :event ::s.e.transactions/do-delete-record-event)
  :ret (s/keys))

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::item-map)
    (st/reg-sub ::item item-sub)
    (st/reg-sub ::items items-sub)
    (st/reg-sub ::items-by-account items-by-account)
    (st/reg-sub ::items-by-currency items-by-currency)
    (st/reg-sub ::items-by-user items-by-user)
    (st/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))
    (st/reg-event-fx ::do-fetch-index-success (partial do-fetch-index-success store))
    (st/reg-event-fx ::do-fetch-index-failed (partial do-fetch-index-failed store))
    (st/reg-event-fx ::do-fetch-index (partial do-fetch-index store))
    (st/reg-basic-sub ::do-submit-state)
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit (partial do-submit store))
    (st/reg-event-fx ::do-delete-record-failed (partial do-delete-record-failed store))
    (st/reg-event-fx ::do-delete-record-success (partial do-delete-record-success store))
    (st/reg-event-fx ::do-delete-record (partial do-delete-record store)))
  store)
