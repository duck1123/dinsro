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

(s/def ::item ::s.categories/item)

;; Item Map

(s/def ::item-map (s/map-of ::ds/id ::s.categories/item))
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

;; Items by User

(defn items-by-user
  [items event]
  (let [[_ id] event]
    (filter #(= id (get-in % [::s.categories/user :db/id])) items)))

(rf/reg-sub ::items-by-user :<- [::items] items-by-user)

;; Create

(defn do-submit-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ _]
  {})

(defn do-submit
  [{:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-categories]
    (:token db)
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
   (e/fetch-request-auth
    [:api-show-categories {:id id}]
    (:token db)
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
  [{:keys [db]} [item]]
  {:http-xhrio
   (e/delete-request-auth
    [:api-show-currency {:id (:db/id item)}]
    (:token db)
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
  [{:keys [db]} _]
  {:http-xhrio
   (e/fetch-request-auth
    [:api-index-categories]
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(s/fdef do-fetch-index
  :args (s/cat :cofx ::s.e.categories/do-fetch-index-cofx
               :event ::s.e.categories/do-fetch-index-event)
  :ret ::s.e.categories/do-fetch-index-response)

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
