(ns dinsro.events.currencies
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def items-sub-default [])

(s/def ::item ::s.currencies/item)

;; Item Map

(s/def ::item-map (s/map-of ::ds/id ::item))
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

;; Create

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
    [:api-index-currencies]
    store
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

;; Read

(s/def ::do-fetch-record-state keyword?)

(defn do-fetch-record-success
  [store {:keys [db]} [{:keys [item]}]]
  {:db (-> db
           (assoc ::do-fetch-record-state :loaded)
           (assoc ::item item)
           (assoc-in [::item-map (:db/id item)] item))})

(s/def ::do-fetch-record-failed-cofx (s/keys))
(s/def ::do-fetch-record-failed-event (s/keys))
(s/def ::do-fetch-record-failed-response (s/keys))

(defn do-fetch-record-failed
  [_store {:keys [db]} _]
  {:db (assoc db ::do-fetch-record-state :failed)})

(s/fdef do-fetch-record-failed
  :args (s/cat :cofx ::do-fetch-record-failed-cofx
               :event ::do-fetch-record-failed-event)
  :ret ::do-fetch-record-failed-response)

(defn do-fetch-record
  [store {:keys [db]} [id]]
  {:db (assoc db ::do-fetch-record-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-show-currency {:id id}]
    store
    (:token db)
    [::do-fetch-record-success]
    [::do-fetch-record-failed])})

;; Delete

(defn do-delete-record-success
  [_store _cofx [{:keys [id]}]]
  {:dispatch [::do-fetch-index id]})

(defn do-delete-record-failed
  [_store {:keys [db]} [{:keys [id]}]]
  {:db (-> db
           (assoc ::delete-record-failed true)
           (assoc ::delete-record-failure-id id))})

(defn do-delete-record
  [store {:keys [db]} [currency]]
  {:http-xhrio
   (e/delete-request-auth
    [:api-show-currency {:id (:db/id currency)}]
    store
    (:token db)
    [::do-delete-record-success]
    [::do-delete-record-failed])})

;; Index

(s/def ::do-fetch-index-state keyword?)

(defn do-fetch-index-success
  [_store {:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::do-fetch-index-state :loaded))})

(s/def ::do-fetch-index-cofx (s/keys))
(s/def ::do-fetch-index-event (s/keys))

(defn do-fetch-index-failed
  [_store _cofx _event]
  {})

(defn do-fetch-index
  [store {:keys [db]} _event]
  {:http-xhrio
   (e/fetch-request-auth
    [:api-index-currencies]
    store
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::item-map)
    (st/reg-sub ::items items-sub)
    (st/reg-sub ::item item-sub)
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit (partial do-submit store))
    (st/reg-sub ::do-fetch-record-state (fn [db _] (get db ::do-fetch-record-state :invalid)))
    (st/reg-event-fx ::do-fetch-record-success (partial do-fetch-record-success store))
    (st/reg-event-fx ::do-fetch-record-failed (partial do-fetch-record-failed store))
    (st/reg-event-fx ::do-fetch-record (partial do-fetch-record store))
    (st/reg-event-fx ::do-delete-record-success (partial do-delete-record-success store))
    (st/reg-event-fx ::do-delete-record-failed (partial do-delete-record-failed store))
    (st/reg-event-fx ::do-delete-record (partial do-delete-record store))
    (st/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))
    (st/reg-event-fx ::do-fetch-index-success (partial do-fetch-index-success store))
    (st/reg-event-fx ::do-fetch-index-failed (partial do-fetch-index-failed store))
    (st/reg-event-fx ::do-fetch-index (partial do-fetch-index store)))
  store)
