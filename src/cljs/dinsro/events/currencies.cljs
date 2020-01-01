(ns dinsro.events.currencies
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [dinsro.spec :as ds]
            [dinsro.spec.currencies :as s.currencies]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def items-sub-default [])

(s/def ::items (s/coll-of ::s.currencies/item))
(rfu/reg-basic-sub ::items)
(rfu/reg-set-event ::items)
(def items ::items)

(s/def ::item-map (s/map-of ::ds/id ::s.currencies/item))
(def item-map ::item-map)

(defn sub-item-map
  [db _]
  (get db ::item-map))

(s/fdef sub-item-map
  :ret ::item-map)

(rf/reg-sub ::item-map               sub-item-map)

(defn item-sub
  [item-map ::item-map [_ id] any?]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :item-map ::item-map
               :event any?)
  :ret ::s.currencies/item)

(rf/reg-sub
 ::item
 :<- [::item-map]
 item-sub)

;; Create

(defn do-submit-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ _]
  {})

(defn do-submit
  [_ [data]]
  {:http-xhrio
   {:method          :post
    :uri             (kf/path-for [:api-index-currencies])
    :params          data
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-submit-success]
    :on-failure      [::do-submit-failed]}})

(kf/reg-event-fx ::do-submit-success do-submit-success)
(kf/reg-event-fx ::do-submit-failed do-submit-failed)
(kf/reg-event-fx ::do-submit do-submit)

;; Read

(s/def ::do-fetch-record-state keyword?)
(rf/reg-sub ::do-fetch-record-state (fn [db _] (get db ::do-fetch-record-state :invalid)))

(defn do-fetch-record-success
  [{:keys [db]} [{:keys [item]}]]
  {:db (-> db
           (assoc ::do-fetch-record-state :loaded)
           (assoc ::item item)
           (assoc-in [::item-map (:db/id item)] item))})

(s/def ::do-fetch-record-failed-cofx (s/keys))
(s/def ::do-fetch-record-failed-event (s/keys))
(s/def ::do-fetch-record-failed-response (s/keys))

(defn do-fetch-record-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-record-state :failed)})

(s/fdef do-fetch-record-failed
  :args (s/cat :cofx ::do-fetch-record-failed-cofx
               :event ::do-fetch-record-failed-event)
  :ret ::do-fetch-record-failed-response)

(defn do-fetch-record
  [{:keys [db]} [id]]
  {:db (assoc db ::do-fetch-record-state :loading)
   :http-xhrio
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
  [_ [{:keys [id]}]]
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

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [cofx event]
  (let [{:keys [db]} cofx
        [{:keys [items]}] event]
    {:db (-> db
             (assoc ::items items)
             (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
             (assoc ::do-fetch-index-state :loaded))}))

(s/def ::do-fetch-index-cofx (s/keys))
(s/def ::do-fetch-index-event (s/keys))

(defn do-fetch-index-failed
  [_ _]
  {})

(defn do-fetch-index
  [_ _]
  {:http-xhrio
   {:method          :get
    :uri             (kf/path-for [:api-index-currencies])
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}})

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
