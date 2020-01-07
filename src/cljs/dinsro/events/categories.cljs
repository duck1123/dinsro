(ns dinsro.events.categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.categories :as s.categories]
   [dinsro.spec.events.categories :as s.e.categories]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(def example-category
  {:db/id 1
   ::s.categories/name "Foo"
   ::s.categories/user {:db/id 12}})

(s/def ::items (s/coll-of ::s.categories/item))
(rfu/reg-basic-sub ::items)
(rfu/reg-set-event ::items)
(def items ::items)

(s/def ::item-map (s/map-of ::ds/id ::s.categories/item))
(rfu/reg-basic-sub ::item-map)
(def item-map ::item-map)

(defn sub-item
  [items [_ target-item]]
  (first (filter #(= (:id %) (:db/id target-item)) items)))

(defn items-by-user
  [db event]
  (let [[_ id] event]
    (filter #(= id (get-in % [::s.categories/user :db/id])) (::items db))))

(rf/reg-sub ::item :<- [::items] sub-item)
(rf/reg-sub ::items-by-user items-by-user)

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
   (e/post-request [:api-index-categories]
                   [::do-submit-success]
                   [::do-submit-failed]
                   data)})

(kf/reg-event-fx ::do-submit-success   do-submit-success)
(kf/reg-event-fx ::do-submit-failed    do-submit-failed)
(kf/reg-event-fx ::do-submit           do-submit)

;; Read

(s/def ::do-fetch-record-state keyword?)
(rf/reg-sub ::do-fetch-record-state (fn [db _] (get db ::do-fetch-record-state :invalid)))

(defn do-fetch-record-success
  [{:keys [db]} [{:keys [item]}]]
  {:db (-> db
           (assoc ::do-fetch-record-state :loaded)
           (assoc ::item item)
           (assoc-in [::item-map (:db/id item)] item))})

(defn do-fetch-record-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-record-state :failed)})

(s/fdef do-fetch-record-failed
  :args (s/cat :cofx ::s.e.categories/do-fetch-record-failed-cofx
               :event ::s.e.categories/do-fetch-record-failed-event)
  :ret ::s.e.categories/do-fetch-record-failed-response)

(defn do-fetch-record
  [{:keys [db]} [id]]
  {:db (assoc db ::do-fetch-record-state :loading)
   :http-xhrio
   (e/fetch-request [:api-show-categories {:id id}]
                    [::do-fetch-record-success]
                    [::do-fetch-record-failed])})

(kf/reg-event-fx ::do-fetch-record-success do-fetch-record-success)
(kf/reg-event-fx ::do-fetch-record-failed  do-fetch-record-failed)
(kf/reg-event-fx ::do-fetch-record         do-fetch-record)

;; Delete

(defn do-delete-record-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_ _]
  {})

(defn do-delete-record
  [_ [item]]
  {:http-xhrio
   (e/delete-request [:api-show-currency {:id (:db/id item)}]
                     [::do-delete-record-success]
                     [::do-delete-record-failed])})

(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record-failed do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record do-delete-record)

;; Index

(rfu/reg-basic-sub ::do-fetch-index-state)

(defn do-fetch-index-success
  [{:keys [db]} [{:keys [items]}]]
  {:db (-> db
        (assoc ::items items)
        (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
        (assoc ::do-fetch-index-state :loaded))})

(defn do-fetch-index-failed
  [_ _]
  {})

(s/fdef do-fetch-index-failed
  :args (s/cat :cofx ::s.e.categories/do-fetch-index-failed-cofx
               :event ::s.e.categories/do-fetch-index-failed-event)
  :ret ::s.e.categories/do-fetch-index-failed-response)

(defn do-fetch-index
  [_ _]
  {:http-xhrio
   (e/fetch-request [:api-index-categories]
                    [::do-fetch-index-success]
                    [::do-fetch-index-failed])})

(s/fdef do-fetch-index
  :args (s/cat :cofx ::s.e.categories/do-fetch-index-cofx
               :event ::s.e.categories/do-fetch-index-event)
  :ret ::s.e.categories/do-fetch-index-response)

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
