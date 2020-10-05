(ns dinsro.events.categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.categories :as s.categories]
   [dinsro.spec.events.categories :as s.e.categories]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def example-category
  {:db/id 1
   ::s.categories/name "Foo"
   ::s.categories/user {:db/id 12}})

(s/def ::item ::s.categories/item)

;; Item Map

(s/def ::item-map (s/map-of ::ds/id ::s.categories/item))
(def item-map ::item-map)

;; Items

(s/def ::items (s/coll-of ::item))

(defn items-sub
  "Subscription handler: Index all items"
  [{:keys [::item-map]} _]
  (sort-by :db/id (vals item-map)))

(s/fdef items-sub
  :args (s/cat :db (s/keys :req [::item-map])
               :event (s/cat :kw keyword?))
  :ret ::items)

;; Item

(defn item-sub
  "Subscription handler: Lookup an item from the item map by id"
  [{:keys [::item-map]} [_ id]]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :db (s/keys :req [::item-map])
               :event (s/cat :kw keyword? :id :db/id))
  :ret ::item)

;; Items by User

(defn items-by-user
  [{:keys [::item-map]} event]
  (let [[_ id] event]
    (filter #(= id (get-in % [::s.categories/user :db/id])) (vals item-map))))

;; Create

(defn do-submit-success
  [_ _ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ _ _]
  {})

(defn do-submit
  [store {:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-categories]
    store
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

;; Read

(s/def ::do-fetch-record-state keyword?)

(defn do-fetch-record-success
  [_ {:keys [db]} [{:keys [item]}]]
  {:db (-> db
           (assoc ::do-fetch-record-state :loaded)
           (assoc ::item item)
           (assoc-in [::item-map (:db/id item)] item))})

(defn do-fetch-record-failed
  [_ {:keys [db]} _]
  {:db (assoc db ::do-fetch-record-state :failed)})

(s/fdef do-fetch-record-failed
  :args (s/cat :cofx ::s.e.categories/do-fetch-record-failed-cofx
               :event ::s.e.categories/do-fetch-record-failed-event)
  :ret ::s.e.categories/do-fetch-record-failed-response)

(defn do-fetch-record
  [store {:keys [db]} [id]]
  {:db (assoc db ::do-fetch-record-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-show-categories {:id id}]
    store
    (:token db)
    [::do-fetch-record-success]
    [::do-fetch-record-failed])})

;; Delete

(defn do-delete-record-success
  [_ _ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_ _ _]
  {})

(defn do-delete-record
  [store {:keys [db]} [item]]
  {:http-xhrio
   (e/delete-request-auth
    [:api-show-currency {:id (:db/id item)}]
    store
    (:token db)
    [::do-delete-record-success]
    [::do-delete-record-failed])})

;; Index

(defn do-fetch-index-success
  [_ {:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::do-fetch-index-state :loaded))})

(defn do-fetch-index-failed
  [_ _ _]
  {})

;; (s/fdef do-fetch-index-failed
;;   :args (s/cat :cofx ::s.e.categories/do-fetch-index-failed-cofx
;;                :event ::s.e.categories/do-fetch-index-failed-event)
;;   :ret ::s.e.categories/do-fetch-index-failed-response)

(defn do-fetch-index
  [store {:keys [db]} _]
  {:http-xhrio
   (e/fetch-request-auth
    [:api-index-categories]
    store
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

;; (s/fdef do-fetch-index
;;   :args (s/cat :cofx ::s.e.categories/do-fetch-index-cofx
;;                :event ::s.e.categories/do-fetch-index-event)
;;   :ret ::s.e.categories/do-fetch-index-response)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::item-map)
    (st/reg-sub ::item item-sub)
    (st/reg-sub ::items items-sub)
    (st/reg-sub ::items-by-user items-by-user)
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit (partial do-submit store))
    (st/reg-sub ::do-fetch-record-state (fn [db _] (get db ::do-fetch-record-state :invalid)))
    (st/reg-event-fx ::do-fetch-record-success (partial do-fetch-record-success store))
    (st/reg-event-fx ::do-fetch-record-failed (partial do-fetch-record-failed store))
    (st/reg-event-fx ::do-fetch-record (partial do-fetch-record store))
    (st/reg-event-fx ::do-delete-record-success (partial do-delete-record-success store))
    (st/reg-event-fx ::do-delete-record-failed (partial do-delete-record-failed store))
    (st/reg-event-fx ::do-delete-record (partial do-delete-record store))
    (st/reg-basic-sub ::do-fetch-index-state)
    (st/reg-event-fx ::do-fetch-index-success (partial do-fetch-index-success store))
    (st/reg-event-fx ::do-fetch-index-failed (partial do-fetch-index-failed store))
    (st/reg-event-fx ::do-fetch-index (partial do-fetch-index store)))
  store)
