(ns dinsro.events.users
  (:require
   [cemerick.url :as url]
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.users :as s.users]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [ring.util.http-status :as status]))

(s/def ::item ::s.users/item)

;; Item Map

(s/def ::item-map (s/map-of ::ds/id ::item))
(rfu/reg-basic-sub ::item-map)
(def item-map ::item-map)

;; Items

(s/def ::items (s/coll-of ::item))

(defn items-sub
  "Subscription handler: Index all items"
  [item-map _]
  (sort-by :db/id (vals item-map)))

(s/fdef items-sub
  :args (s/cat :item-map ::item-map
               :event (s/cat :kw keyword?))
  :ret ::items)

(rf/reg-sub ::items :<- [::item-map] items-sub)

;; Item

(defn item-sub
  "Subscription handler: Lookup an item from the item map by id"
  [item-map [_ id]]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :item-map ::item-map
               :event (s/cat :kw keyword? :id :db/id))
  :ret ::item)

(rf/reg-sub ::item :<- [::item-map] item-sub)

;; Read

(s/def ::do-fetch-record-state keyword?)
(rf/reg-sub ::do-fetch-record-state (fn [db _] (get db ::do-fetch-record-state :invalid)))

(defn do-fetch-record-success
  [{:keys [db]} [{:keys [item]}]]
  {:db (-> db
           (assoc ::do-fetch-record-state :loaded)
           (assoc ::item item)
           (assoc-in [::item-map (:db/id item)] item))})

(defn do-fetch-record-unauthorized
  [{:keys [db]} _]
  (let [match (:kee-frame/route db)]
    {:db (assoc db :return-to match)
     :navigate-to [:login-page]}))

(defn do-fetch-record-failed
  [{:keys [db]} [{:keys [status] :as request}]]
  (if (= status/forbidden status)
    {:dispatch [::do-fetch-record-unauthorized request]}
    {:db (assoc db ::do-fetch-record-state :failed)}))

(defn do-fetch-record
  [{:keys [db]} [id]]
  {:db (assoc db ::do-fetch-record-state :loading)
   :http-xhrio
   (e/fetch-request
    [:api-show-user {:id id}]
    [::do-fetch-record-success]
    [::do-fetch-record-failed])})

(kf/reg-event-fx ::do-fetch-record-success       do-fetch-record-success)
(kf/reg-event-fx ::do-fetch-record-failed        do-fetch-record-failed)
(kf/reg-event-fx ::do-fetch-record-unauthorized  do-fetch-record-unauthorized)
(kf/reg-event-fx ::do-fetch-record               do-fetch-record)

;; Delete

(defn do-delete-record-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [{:keys [db]} [{:keys [id]}]]
  {:db (-> db
           (assoc :failed true)
           (assoc :delete-record-failure-id id))})

(defn do-delete-record
  [_ [user]]
  {:http-xhrio
   (e/delete-request
    [:api-show-user {:id (:db/id user)}]
    [::do-delete-record-success]
    [::do-delete-record-failed])})

(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record-failed do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record do-delete-record)

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [{:keys [db]} [{items :users}]]
  {:db (-> db
           (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::do-fetch-index-state :loaded))})

(defn do-fetch-index-unauthorized
  [cofx _]
  (let [_route (get-in cofx [:db :kee-frame/route :data])]
    {:navigate-to [:login-page {:query-string (url/map->query {:return-to "/users"})}]}))

(defn do-fetch-index-failed
  [{:keys [db]} [response]]
  (if (= status/forbidden (:status response))
    {:dispatch [::do-fetch-index-unauthorized response]}
    {:db (assoc db ::do-fetch-index-state :failed)}))

(defn do-fetch-index
  [_ _]
  {:http-xhrio
   (e/fetch-request
    [:api-index-users]
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-unauthorized do-fetch-index-unauthorized)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
