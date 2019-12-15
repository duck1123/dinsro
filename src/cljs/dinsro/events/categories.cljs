(ns dinsro.events.categories
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.spec.categories :as s.categories]
            [dinsro.spec.events.categories :as s.e.categories]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::items)

(defn sub-item
  [items [_ target-item]]
  (first (filter #(= (:id %) (:db/id target-item)) items)))

(defn-spec items-by-user (s/coll-of ::s.categories/item)
  [db any? event any?]
  (let [[_ id] event]
    (filter #(= id (get-in % [::s.categories/user :db/id])) (::items db))))

(rf/reg-sub ::item :<- [::items] sub-item)
(rf/reg-sub ::items-by-user items-by-user)

;; Create

(defn do-submit-success
  [_ data]
  (timbre/info "Submit success" data)
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ [response]]
  (timbre/info "Submit failed" (get-in response [:parse-error :status-text])))

(defn do-submit
  [{:keys [db]} [data]]
  {:http-xhrio
   {:method          :post
    :uri             (kf/path-for [:api-index-categories])
    :params          data
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-submit-success]
    :on-failure      [::do-submit-failed]}})

(kf/reg-event-fx ::do-submit-success   do-submit-success)
(kf/reg-event-fx ::do-submit-failed    do-submit-failed)
(kf/reg-event-fx ::do-submit           do-submit)

;; Delete

(defn do-delete-record-success
  [_ _]
  (timbre/info "delete account success")
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_ _]
  (timbre/info "delete account failed")
  {})

(defn do-delete-record
  [_ [id]]
  {:http-xhrio
   {:uri             (kf/path-for [:api-show-currency {:id id}])
    :method          :delete
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-delete-record-success]
    :on-failure      [::do-delete-record-failed]}})

(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record-failed do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record do-delete-record)

;; Index

(rfu/reg-basic-sub ::do-fetch-index-state)

(defn do-fetch-index-success
  [db [{:keys [items]}]]
  (timbre/info "fetch records success" items)
  (-> db
      (assoc ::items items)
      (assoc ::do-fetch-index-state :loaded)))

(defn-spec do-fetch-index-failed ::s.e.categories/do-fetch-index-failed-response
  [_ ::s.e.categories/do-fetch-index-failed-cofx
   _ ::s.e.categories/do-fetch-index-failed-event]
  (timbre/info "fetch records failed")
  {})

(defn-spec do-fetch-index ::s.e.categories/do-fetch-index-response
  [_ ::s.e.categories/do-fetch-index-cofx
   _ ::s.e.categories/do-fetch-index-event]
  {:http-xhrio
   {:uri             (kf/path-for [:api-index-categories])
    :method          :get
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}})

(kf/reg-event-db ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
