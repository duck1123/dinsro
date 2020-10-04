(ns dinsro.events.accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.events.accounts :as s.e.accounts]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(s/def ::item ::s.accounts/item)

;; Item Map

(s/def ::item-map (s/map-of ::ds/id ::s.accounts/item))
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

(s/def ::item-sub-event (s/tuple keyword? :db/id))

(defn item-sub
  "Subscription handler: Lookup an item from the item map by id"
  [{:keys [::item-map]} [_ id]]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :db (s/keys :req [::item-map])
               :event ::item-sub-event)
  :ret (s/nilable ::item))

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

;; Delete

(s/def ::do-delete-record-state ::ds/state)

(defn do-delete-record-success
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_store _cofx _event]
  {})

(defn do-delete-record
  [store {:keys [db]} [item]]
  {:http-xhrio
   (e/delete-request-auth
    [:api-show-account {:id (:db/id item)}]
    store
    (:token db)
    [::do-delete-record-success]
    [::do-delete-record-failed])})

;; Index

(s/def ::do-fetch-index-state ::ds/state)
(def do-fetch-index-state ::do-fetch-index-state)

(defn do-fetch-index-success
  [_store {:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::do-fetch-index-state :loaded))})

(defn do-fetch-index-failed
  [_store {:keys [db]} _event]
  {:db (assoc db ::do-fetch-index-state :failed)})

(s/fdef do-fetch-index-failed
  :args (s/cat :cofx ::s.e.accounts/do-fetch-index-failed-cofx
               :event ::s.e.accounts/do-fetch-index-failed-event)
  :ret ::s.e.accounts/do-fetch-index-failed-response)

(defn do-fetch-index
  [store {:keys [db]} _event]
  {:db (assoc db ::do-fetch-index-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-index-accounts]
    store
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(s/fdef do-fetch-index
  :args (s/cat :cofx ::s.e.accounts/do-fetch-index-cofx
               :event ::s.e.accounts/do-fetch-index-event)
  :ret ::s.e.accounts/do-fetch-index-response)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::item-map)
    (st/reg-sub ::item item-sub)
    (st/reg-sub ::items items-sub)
    (st/reg-sub ::items-by-currency items-by-currency)
    (st/reg-sub ::items-by-user items-by-user)
    (st/reg-basic-sub ::do-submit-state)
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit (partial do-submit store))
    (st/reg-basic-sub ::do-delete-record-state)
    (st/reg-event-fx ::do-delete-record-success (partial do-delete-record-success store))
    (st/reg-event-fx ::do-delete-record-failed (partial do-delete-record-failed store))
    (st/reg-event-fx ::do-delete-record (partial do-delete-record store))
    (st/reg-basic-sub ::do-fetch-index-state)
    (st/reg-event-fx ::do-fetch-index-success (partial do-fetch-index-success store))
    (st/reg-event-fx ::do-fetch-index-failed (partial do-fetch-index-failed store))
    (st/reg-event-fx ::do-fetch-index (partial do-fetch-index store)))
  store)
