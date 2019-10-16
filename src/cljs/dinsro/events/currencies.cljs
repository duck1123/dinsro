(ns dinsro.events.currencies
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::currencies                  (fn [db _] (get db ::currencies [])))
(rf/reg-sub ::do-fetch-currencies-loading (fn [db _] (get db ::do-fetch-currencies-loading false)))

(kf/reg-event-db
 ::do-fetch-currencies-success
 (fn [db [_ {:keys [currencies]}]]
   (timbre/info "fetch accounts success" currencies)
   (assoc db ::currencies currencies)))

(kf/reg-event-fx
 ::do-fetch-currencies-failed
 (fn [_ _]
   (timbre/info "fetch accounts failed")))

(kf/reg-event-fx
 ::do-fetch-currencies
 (fn-traced
  [{:keys [db]} [_ data]]
  {:db (assoc db ::do-fetch-currencies-loading true)
   :http-xhrio
   {:method          :post
    :uri             "/api/v1/currencies"
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-currencies-succeeded]
    :on-failure      [::do-fetch-currencies-failed]}}))
