(ns dinsro.events.users
  (:require
   [ajax.core :as ajax]
   [cemerick.url :as url]
   [clojure.spec.alpha :as s]
   [dinsro.spec :as ds]
   [dinsro.spec.users :as s.users]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [ring.util.http-status :as status]))

(s/def ::items (s/coll-of ::s.users/item))
(rfu/reg-basic-sub ::items)

(s/def ::item (s/nilable ::s.users/item))

(s/def ::item-map (s/map-of ::ds/id ::s.users/item))
(rfu/reg-basic-sub ::item-map)
(def item-map ::item-map)

(defn item-sub
  [db [_kw id]]
  (get-in db [::item-map id]))

(rf/reg-sub ::item item-sub)

(defn filter-records
  [db [_kw id]]
  (->> @(rf/subscribe [::items])
       (keep #(when (not= (:db/id %) id) %))
       (assoc db ::items)))

(kf/reg-event-db ::filter-records filter-records)

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
   {:uri             (kf/path-for [:api-show-user {:id id}])
    :method          :get
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-record-success]
    :on-failure      [::do-fetch-record-failed]}})

(kf/reg-event-fx ::do-fetch-record-success       do-fetch-record-success)
(kf/reg-event-fx ::do-fetch-record-failed        do-fetch-record-failed)
(kf/reg-event-fx ::do-fetch-record-unauthorized  do-fetch-record-unauthorized)
(kf/reg-event-fx ::do-fetch-record               do-fetch-record)

;; Delete

(defn do-delete-record-success
  [_ [{:keys [id]}]]
  {:dispatch [::filter-records id]})

(defn do-delete-record-failed
  [{:keys [db]} [{:keys [id]}]]
  {:db (-> db
           (assoc :failed true)
           (assoc :delete-record-failure-id id))})

(defn do-delete-record
  [_ [user]]
  {:http-xhrio
   {:uri             (kf/path-for [:api-show-user {:id (:db/id user)}])
    :method          :delete
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-delete-record-success]
    :on-failure      [::do-delete-record-failed]}})

(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record-failed do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record do-delete-record)

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [{:keys [db]} [{items :users}]]
  {:db (-> db
           (assoc ::items items)
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
   {:uri             (kf/path-for [:api-index-users])
    :method          :get
    :timeout         8000
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}})

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-unauthorized do-fetch-index-unauthorized)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
