(ns dinsro.events.transactions
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items                  (fn [db _] (get db ::items                  [])))
(rf/reg-sub ::do-fetch-index-loading (fn [db _] (get db ::do-fetch-index-loading false)))

(rf/reg-event-db
 ::do-fetch-index-success
 (fn [db [_ {:keys [items]}]]
   (timbre/info "fetch records success" items)
   (assoc db ::items items)))

(rf/reg-event-fx
 ::do-fetch-index-failed
 (fn [_ _]
   (timbre/info "fetch records failed")))

(rf/reg-event-fx
 ::do-fetch-index
 (fn-traced
   [{:keys [db]} [_ data]]
   {:db (assoc db ::do-fetch-index-loading true)
    :http-xhrio
    {:method          :get
     :uri             "/api/v1/transactions"
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-fetch-index-success]
     :on-failure      [::do-fetch-index-failed]}}))
