(ns dinsro.events.currencies
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items                  (fn [db _] (get db ::items                  [])))

(defn sub-item-map
  [db event]
  (timbre/spy :info db)
  (timbre/spy :info event)
  (get db ::item-map))

(rf/reg-sub ::item-map               sub-item-map)
(rf/reg-sub ::do-fetch-index-loading (fn [db _] (get db ::do-fetch-index-loading false)))

(rf/reg-sub
 ::item
 :<- [::item-map]
 (fn-traced [item-map [_ id]]
   (get item-map id)
   #_(first (filter #(= (:db/id %) id) items))))

;; Create

(defn do-submit-success
  [_ data]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [[_ response]]
  (timbre/info "Submit failed" response))

(defn do-submit
  [{:keys [db]} [data]]
  {:db (assoc db ::do-submit-loading true)
   :http-xhrio
   {:method          :post
    :uri             (kf/path-for [:api-index-currencies])
    :params          data
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-submit-succeeded]
    :on-failure      [::do-submit-failed]}})

(kf/reg-event-fx ::do-submit-success do-submit-success)
(kf/reg-event-fx ::do-submit-failed do-submit-failed)
(kf/reg-event-fx ::do-submit do-submit)

;; Read

(defn do-fetch-record-success
  [{:keys [db]} [{:keys [item]}]]
  (timbre/spy :info item)
  {:db (-> db
           (assoc ::item item)
           (assoc-in [::item-map (:db/id item)] item))})

(defn do-fetch-record-failed
  []
  {})

(defn do-fetch-record
  [_ [id]]
  {:http-xhrio
   {:uri             (kf/path-for [:api-show-currency {:id id}])
    :method          :get
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-record-success]
    :on-failure      [::do-fetch-record-failed]}})

(kf/reg-event-fx ::do-fetch-record-success do-fetch-record-success)
(kf/reg-event-fx ::do-fetch-record-failed  do-fetch-record-failed)
(kf/reg-event-fx ::do-fetch-record         do-fetch-record)

;; Delete

(defn do-delete-record-success
  [cofx [{:keys [id]}]]
  {:dispatch [::do-fetch-index id]})

(defn do-delete-record-failed
  [db [{:keys [id]}]]
  (-> db
      (assoc ::delete-record-failed true)
      (assoc ::delete-record-failure-id id)))

(defn do-delete-record
  [_ [currency]]
  {:http-xhrio
   {:uri             (kf/path-for [:api-show-currency {:id (:db/id currency)}])
    :method          :delete
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-delete-record-success]
    :on-failure      [::do-delete-record-failed]}})

(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-db ::do-delete-record-failed do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record do-delete-record)

;; Index

(defn do-fetch-index-success
  [db [{:keys [items]}]]
  (assoc db ::items items))

(defn do-fetch-index-failed
  [_ _]
  (timbre/info "fetch records failed"))

(defn do-fetch-index
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-loading true)
   :http-xhrio
   {:method          :get
    :uri             (kf/path-for [:api-index-currencies])
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}})

(kf/reg-event-db ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
