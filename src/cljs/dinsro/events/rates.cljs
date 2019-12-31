(ns dinsro.events.rates
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(s/def ::items                   (s/coll-of ::s.rates/item))
(rf/reg-sub ::items              (fn [db _] (get db ::items [])))

(s/def ::items-by-currency-event (s/cat :keyword keyword? :currency ::s.currencies/item))

(defn items-by-currency
  [items [_ {:keys [db/id]}]]
  (filter #(= (get-in % [::s.rates/currency :db/id]) id) items))

(s/fdef items-by-currency
  :args (s/cat :items ::items :event ::items-by-currency-event)
  :ret ::items)

(rf/reg-sub ::items-by-currency :<- [::items] items-by-currency)

(rf/reg-sub
 ::item
 :<- [::items]
 (fn [items [_ id]]
   (first (filter #(= (:id %) id) items))))

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [cofx event]
  (let [{:keys [db]} cofx
        [{:keys [items]}] event
        items (map (fn [item] (update item ::s.rates/date #(js/Date. %))) items)]
    {:db (-> db
             (assoc ::items items)
             (assoc ::do-fetch-index-state :loaded))}))

(defn do-fetch-index-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn do-fetch-index
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :loading)
   :http-xhrio
   {:method          :get
    :uri             (kf/path-for [:api-index-rates])
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}})

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)

;; Submit

(defn do-submit-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit
  [_ [data]]
  {:http-xhrio
   {:method          :post
    :uri             (kf/path-for [:api-index-rates])
    :params          data
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-submit-success]
    :on-failure      [::do-submit-failed]}})

(s/fdef do-submit
  :ret (s/keys))

(kf/reg-event-fx ::do-submit-failed  do-submit-failed)
(kf/reg-event-fx ::do-submit-success do-submit-success)
(kf/reg-event-fx ::do-submit         do-submit)

;; Delete

(s/def ::do-delete-record-success-cofx (s/keys))
(s/def ::do-delete-record-failed-cofx (s/keys))
(s/def ::do-delete-record-cofx (s/keys))
(s/def ::do-delete-record-event (s/cat :item ::s.rates/item))

(defn do-delete-record-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(s/fdef do-delete-record-success
  :args (s/cat :cofx ::do-delete-record-success-cofx
               :event any?)
  :ret (s/keys))

(defn do-delete-record-failed
  [_ _]
  {:dispatch [::do-fetch-index]})

(s/fdef do-delete-record-failed
  :args (s/cat :cofx ::do-delete-record-failed-cofx
               :event any?)
  :ret (s/keys))

(defn do-delete-record
  [_ [item]]
  (let [id (:db/id item)]
    {:http-xhrio
     {:uri             (kf/path-for [:api-show-rate {:id id}])
      :method          :delete
      :format          (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})
      :on-success      [::do-delete-record-success]
      :on-failure      [::do-delete-record-failed]}}))

(s/fdef do-delete-record
  :args (s/cat :cofx ::do-delete-record-cofx
               :event ::do-delete-record-event)
  :ret (s/keys))

(kf/reg-event-fx ::do-delete-record-failed  do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record         do-delete-record)
