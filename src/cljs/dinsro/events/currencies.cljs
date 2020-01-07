(ns dinsro.events.currencies
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(def items-sub-default [])

(s/def ::item ::s.currencies/item)

;; Item Map

(s/def ::item-map (s/map-of ::ds/id ::item))
(rfu/reg-basic-sub ::item-map)
(def item-map ::item-map)

;; Items

(s/def ::items (s/coll-of ::item))

(defn items-sub
  "Subscription handler: Index all items"
  [item-map _]
  (sort-by :db/id (vals item-map)))

(s/fdef items-sub
  :args (s/cat :item-map ::item-map
               :event (s/cat :kw keyword?))
  :ret ::items)

(rf/reg-sub ::items :<- [::item-map] items-sub)

;; Item

(defn item-sub
  "Subscription handler: Lookup an item from the item map by id"
  [item-map [_ id]]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :item-map ::item-map
               :event (s/cat :kw keyword? :id :db/id))
  :ret ::item)

(rf/reg-sub ::item :<- [::item-map] item-sub)

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
   (e/post-request [:api-index-currencies]
                   [::do-submit-success]
                   [::do-submit-failed]
                   data)})

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
   (e/fetch-request [:api-show-currency {:id id}]
                    [::do-fetch-record-success]
                    [::do-fetch-record-failed])})

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
   (e/delete-request [:api-show-currency {:id (:db/id currency)}]
                     [::do-delete-record-success]
                     [::do-delete-record-failed])})

(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-db ::do-delete-record-failed do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record do-delete-record)

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [{:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::do-fetch-index-state :loaded))})

(s/def ::do-fetch-index-cofx (s/keys))
(s/def ::do-fetch-index-event (s/keys))

(defn do-fetch-index-failed
  [_ _]
  {})

(defn do-fetch-index
  [_ _]
  {:http-xhrio
   (e/fetch-request [:api-index-currencies]
                    [::do-fetch-index-success]
                    [::do-fetch-index-failed])})

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
