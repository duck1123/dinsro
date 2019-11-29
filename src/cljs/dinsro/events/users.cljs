(ns dinsro.events.users
  (:require [ajax.core :as ajax]
            [cemerick.url :as url]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::loading (fn [db _] (get db ::loading false)))
(rf/reg-sub ::items   (fn [db _] (get db ::items   [])))

(s/def ::item (s/keys ))
(s/def ::items (s/coll-of ::item))

(rf/reg-sub
 ::item
 :<- [::items]
 (fn [items [_ id]]
   (first (filter #(= (:db/id %) id) items))))

(kf/reg-event-db
 ::do-fetch-records-success
 (fn [db [{users :users}]]
   (assoc db ::items users)))

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

(s/def ::do-fetch-record-failed-cofx (s/keys))
(s/def ::do-fetch-record-failed-event (s/keys))
(s/def ::do-fetch-record-failed-response (s/keys))

(defn-spec do-fetch-record-failed ::do-fetch-record-failed-response
  [cofx ::do-fetch-record-failed-cofx
   event ::do-fetch-record-failed-event]
  (timbre/spy :info event)
  (let [{:keys [db]} (timbre/spy :info cofx)]
    {:db (assoc db ::do-fetch-record-state :failed)}))

(defn do-fetch-record
  [cofx event]
  (let [{:keys [db]} cofx
        [id] event]
    {:db (assoc db ::do-fetch-record-state :loading)
     :http-xhrio
     {:uri             (kf/path-for [:api-show-user {:id id}])
      :method          :get
      :response-format (ajax/json-response-format {:keywords? true})
      :on-success      [::do-fetch-record-success]
      :on-failure      [::do-fetch-record-failed]}}))

(kf/reg-event-fx ::do-fetch-record-success do-fetch-record-success)
(kf/reg-event-fx ::do-fetch-record-failed  do-fetch-record-failed)
(kf/reg-event-fx ::do-fetch-record         do-fetch-record)

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

(kf/reg-event-fx
 ::do-fetch-records-failed
 (fn [{:keys [db]} [response]]
   (let [s (:status response)]
     (if (= s 403)
       {:navigate-to [:login-page {:query-string (url/map->query {:return-to "/users"})}]}
       {:db (assoc db :failed true)}))))

(kf/reg-event-fx
 ::do-fetch-records
 (fn [_ _]
   {:http-xhrio
    {:uri             (kf/path-for [:api-index-users])
     :method          :get
     :timeout         8000
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-fetch-records-success]
     :on-failure      [::do-fetch-records-failed]}}))
