(ns dinsro.events.currencies
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items                  (fn [db _] (get db ::items                  [])))
(rf/reg-sub ::do-fetch-index-loading (fn [db _] (get db ::do-fetch-index-loading false)))

(rf/reg-sub
 ::item
 :<- [::items]
 (fn [items [_ id]]
   (first (filter #(= (:id %) id) items))))

(kf/reg-event-db
 ::do-fetch-index-success
 (fn-traced [db [_ {:keys [items]}]]
   (timbre/info "fetch records success" items)
   (assoc db ::items items)))

(kf/reg-event-fx
 ::do-fetch-index-failed
 (fn-traced [_ _]
   (timbre/info "fetch records failed")))

(kf/reg-event-fx
 ::do-fetch-index
 (fn-traced
  [{:keys [db]} [_ data]]
  {:db (assoc db ::do-fetch-index-loading true)
   :http-xhrio
   {:method          :get
    :uri             "/api/v1/currencies"
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}}))
