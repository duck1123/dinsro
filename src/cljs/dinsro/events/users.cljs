(ns dinsro.events.users
  (:require
   [cemerick.url :as url]
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.users :as s.users]
   [dinsro.store :as st]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]))

(s/def ::item ::s.users/item)

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

;; Read

(s/def ::do-fetch-record-state keyword?)

(defn do-fetch-record-success
  [_store {:keys [db]} [{:keys [item]}]]
  {:db (-> db
           (assoc ::do-fetch-record-state :loaded)
           (assoc ::item item)
           (assoc-in [::item-map (:db/id item)] item))})

(defn do-fetch-record-unauthorized
  [_store {:keys [db]} _]
  (let [match (:kee-frame/route db)]
    {:db (assoc db :return-to match)
     :navigate-to [:login-page]}))

(defn do-fetch-record-failed
  [_store {:keys [db]} [{:keys [status] :as request}]]
  (if (= status/forbidden status)
    {:dispatch [::do-fetch-record-unauthorized request]}
    {:db (assoc db ::do-fetch-record-state :failed)}))

(defn do-fetch-record
  [store {:keys [db]} [id]]
  {:db (assoc db ::do-fetch-record-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-show-user {:id id}]
    store
    (:token db)
    [::do-fetch-record-success]
    [::do-fetch-record-failed])})

;; Delete

(defn do-delete-record-success
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_store {:keys [db]} [{:keys [id]}]]
  {:db (-> db
           (assoc :failed true)
           (assoc :delete-record-failure-id id))})

(defn do-delete-record
  [store {:keys [db]} [user]]
  {:http-xhrio
   (e/delete-request-auth
    [:api-show-user {:id (:db/id user)}]
    store
    (:token db)
    [::do-delete-record-success]
    [::do-delete-record-failed])})

;; Index

(s/def ::do-fetch-index-state keyword?)

(defn do-fetch-index-success
  [_store {:keys [db]} [{items :users}]]
  {:db (-> db
           (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::do-fetch-index-state :loaded))})

(defn do-fetch-index-unauthorized
  [_store cofx _event]
  (let [_route (get-in cofx [:db :kee-frame/route :data])]
    {:navigate-to [:login-page {:query-string (url/map->query {:return-to "/users"})}]}))

(defn do-fetch-index-failed
  [_store {:keys [db]} [response]]
  (if (= status/forbidden (:status response))
    {:dispatch [::do-fetch-index-unauthorized response]}
    {:db (assoc db ::do-fetch-index-state :failed)}))

(defn do-fetch-index
  [store {:keys [db]} _]
  {:http-xhrio
   (e/fetch-request-auth
    [:api-index-users]
    store
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::item-map)
    (st/reg-sub ::item item-sub)
    (st/reg-sub ::items items-sub)
    (st/reg-sub ::do-fetch-record-state (fn [db _] (get db ::do-fetch-record-state :invalid)))
    (st/reg-event-fx ::do-fetch-record-success (partial do-fetch-record-success store))
    (st/reg-event-fx ::do-fetch-record-failed (partial do-fetch-record-failed store))
    (st/reg-event-fx ::do-fetch-record-unauthorized (partial do-fetch-record-unauthorized store))
    (st/reg-event-fx ::do-fetch-record (partial do-fetch-record store))
    (st/reg-event-fx ::do-delete-record-success (partial do-delete-record-success store))
    (st/reg-event-fx ::do-delete-record-failed (partial do-delete-record-failed store))
    (st/reg-event-fx ::do-delete-record (partial do-delete-record store))
    (st/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))
    (st/reg-event-fx ::do-fetch-index-success (partial do-fetch-index-success store))
    (st/reg-event-fx ::do-fetch-index-unauthorized (partial do-fetch-index-unauthorized store))
    (st/reg-event-fx ::do-fetch-index-failed (partial do-fetch-index-failed store))
    (st/reg-event-fx ::do-fetch-index (partial do-fetch-index store)))
  store)
