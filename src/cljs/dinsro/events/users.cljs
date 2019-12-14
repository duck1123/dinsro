(ns dinsro.events.users
  (:require [ajax.core :as ajax]
            [cemerick.url :as url]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [reframe-utils.core :as rfu]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(s/def ::items (s/coll-of ::s.users/item))
(rfu/reg-basic-sub ::items)

(s/def ::item (s/nilable ::s.users/item))
(rf/reg-sub
 ::item
 (fn [db [_ id]]
   (get-in db [::item-map id])))

(kf/reg-event-db
 ::filter-records
 (fn [db [_ id]]
   (->> @(rf/subscribe [::items])
        (keep #(when (not= (:db/id %) id) %))
        (assoc db ::items))))

;; Read

(s/def ::do-fetch-record-state keyword?)
(rf/reg-sub ::do-fetch-record-state (fn [db _] (get db ::do-fetch-record-state :invalid)))

(defn do-fetch-record-success
  [cofx event]
  (let [{:keys [db]} cofx
        [{:keys [item]}] event]
    {:db (-> db
             (assoc ::do-fetch-record-state :loaded)
             (assoc ::item item)
             (assoc-in [::item-map (:db/id item)] item))}))

(defn do-fetch-record-unauthorized
  [cofx event]
  (let [{:keys [db]} cofx
        match (:kee-frame/route db)]
    {:db (assoc db :return-to match)
     :navigate-to [:login-page]}))

;; (defn do-fetch-record-unauthorized
;;   [_ _]
;;   {:navigate-to [:login-page {:query-string (url/map->query {:return-to "/users"})}]})

(s/def ::do-fetch-record-failed-cofx (s/keys))
(s/def ::do-fetch-record-failed-event (s/keys))
(s/def ::do-fetch-record-failed-response (s/keys))

(defn-spec do-fetch-record-failed ::do-fetch-record-failed-response
  [cofx ::do-fetch-record-failed-cofx
   event ::do-fetch-record-failed-event]
  (let [{:keys [db]} cofx
        [{:keys [status] :as request}] event]
    (if (= status/forbidden status)
      {:dispatch [::do-fetch-record-unauthorized request]}
      {:db (assoc db ::do-fetch-record-state :failed)})))

(s/def ::do-fetch-record-cofx (s/keys))
(s/def ::do-fetch-record-event (s/keys))
(s/def ::do-fetch-record-response (s/keys))

(defn-spec do-fetch-record ::do-fetch-record-response
  [cofx ::do-fetch-record-cofx event ::do-fetch-record-event]
  (let [{:keys [db]} cofx
        [id] event]
    {:db (assoc db ::do-fetch-record-state :loading)
     :http-xhrio
     {:uri             (kf/path-for [:api-show-user {:id id}])
      :method          :get
      :response-format (ajax/json-response-format {:keywords? true})
      :on-success      [::do-fetch-record-success]
      :on-failure      [::do-fetch-record-failed]}}))

(kf/reg-event-fx ::do-fetch-record-success       do-fetch-record-success)
(kf/reg-event-fx ::do-fetch-record-failed        do-fetch-record-failed)
(kf/reg-event-fx ::do-fetch-record-unauthorized  do-fetch-record-unauthorized)
(kf/reg-event-fx ::do-fetch-record               do-fetch-record)

;; Delete

(kf/reg-event-fx
 ::do-delete-record-success
 (fn [cofx [{:keys [id]}]]
   {:dispatch [::filter-records id]}))

(kf/reg-event-db
 ::do-delete-record-failed
 (fn [db [{:keys [id]}]]
   (-> db
       (assoc :failed true)
       (assoc :delete-record-failure-id id))))

(kf/reg-event-fx
 ::do-delete-record
 (fn [_ [user]]
   {:http-xhrio
    {:uri             (kf/path-for [:api-show-user {:id (:db/id user)}])
     :method          :delete
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-delete-record-success]
     :on-failure      [::do-delete-record-failed]}}))

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [db [{items :users}]]
  (-> db
      (assoc ::items items)
      (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
      (assoc ::do-fetch-index-state :loaded)))

(defn do-fetch-index-unauthorized
  [cofx event]
  (let [route (timbre/spy :info (get-in cofx [:db :kee-frame/route :data]))]
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

(kf/reg-event-db ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-unauthorized do-fetch-index-unauthorized)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
