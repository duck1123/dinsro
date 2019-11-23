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
 (fn-traced [items [_ id]]
   (timbre/spy :info id)
   (first (filter #(= (:db/id %) id) items))))

(kf/reg-event-db
 ::do-fetch-index-success
 (fn-traced [db [{:keys [items]}]]
   (assoc db ::items items)))

(kf/reg-event-fx
 ::do-fetch-index-failed
 (fn-traced [_ _]
   (timbre/info "fetch records failed")))

(kf/reg-event-fx
 ::do-fetch-index
 (fn-traced [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-loading true)
   :http-xhrio
   {:method          :get
    :uri             (kf/path-for [:api-index-currencies])
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}}))

(kf/reg-event-fx
 ::do-submit-succeeded
 (fn-traced [_ data]
   {:dispatch [::do-fetch-index]}))

(kf/reg-event-fx
 ::do-submit-failed
 (fn-traced [[_ response]]
   (timbre/info "Submit failed" response)))

(kf/reg-event-fx
 ::do-submit
 (fn-traced [{:keys [db]} [data]]
   {:db (assoc db ::do-submit-loading true)
    :http-xhrio
    {:method          :post
     :uri             (kf/path-for [:api-index-currencies])
     :params          data
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-submit-succeeded]
     :on-failure      [::do-submit-failed]}}))

(kf/reg-event-fx
 ::do-delete-record-success
 (fn [cofx [{:keys [id]}]]
   {:dispatch [::do-fetch-index id]}))

(kf/reg-event-db
 ::do-delete-record-failed
 (fn [db [{:keys [id]}]]
   (-> db
       (assoc ::delete-record-failed true)
       (assoc ::delete-record-failure-id id))))

(kf/reg-event-fx
 ::do-delete-record
 (fn [_ [currency]]
   {:http-xhrio
    {:uri             (kf/path-for [:api-show-currency {:id (:db/id (timbre/spy :info currency))}])
     :method          :delete
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-delete-record-success]
     :on-failure      [::do-delete-record-failed]}}))
