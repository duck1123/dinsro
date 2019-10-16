(ns dinsro.events.users
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::loading (fn [db _] (get db ::loading false)))
(rf/reg-sub ::users   (fn [db _] (get db ::users [])))

(kf/reg-event-db
 ::do-fetch-users-success
 (fn [db [{users :users}]]
   (assoc db ::users users)))

(kf/reg-event-db
 :filter-user
 (fn [db [_ id]]
   (->> @(rf/subscribe [::users])
        (keep #(when (not= (:id %) id) %))
        (assoc db ::users))))

(kf/reg-event-fx
 ::do-delete-user-success
 (fn [cofx [{:keys [id]}]]
   {:dispatch [:filter-user id]}))

(kf/reg-event-db
 ::do-fetch-users-failed
 (fn [db event] (assoc db :failed true)))

(kf/reg-event-db
 ::do-delete-user-failed
 (fn [db [{:keys [id]}]]
   (-> db
       (assoc :failed true)
       (assoc :delete-user-failure-id id))))

(kf/reg-event-fx
 ::do-fetch-users
 (fn [_ _]
   {:http-xhrio
    {:uri             "/api/v1/users"
     :method          :get
     :timeout         8000
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-fetch-users-success]
     :on-failure      [::do-fetch-users-failed]}}))

(kf/reg-event-fx
 ::do-delete-user
 (fn [cofx [{:keys [id] :as user}]]
   {:http-xhrio
    {:uri             (str "/api/v1/users/" id)
     :method          :delete
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-delete-user-success]
     :on-failure      [::do-delete-user-failed]}}))
