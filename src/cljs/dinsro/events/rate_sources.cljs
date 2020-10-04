(ns dinsro.events.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec.rate-sources :as s.rate-sources]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(s/def ::item ::s.rate-sources/item)

;; Item Map

(s/def ::item-map (s/map-of :db/id ::item))
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

;; Index

(s/def ::do-fetch-index-state keyword?)

(defn do-fetch-index-success
  [_ {:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::do-fetch-index-state :loaded))})

(defn do-fetch-index-failed
  [_ {:keys [db]} _]
  (timbre/info "fetch records failed")
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn do-fetch-index
  [store {:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-index-rate-sources]
    store
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

;; Submit

(defn do-submit-success
  [_ _ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ _ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit
  [store {:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-rate-sources]
    store
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

;; Delete

(defn do-delete-record-success
  [_ _ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_ _ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record
  [store {:keys [db]} [item]]
  {:http-xhrio
   (e/delete-request-auth
    [:api-show-rate-source {:id (:db/id item)}]
    store
    (:token db)
    [::do-delete-record-success]
    [::do-delete-record-failed])})

(defn do-run-source-failed
  [_ _ _]
  {})

(defn do-run-source-success
  [_ _ _]
  {})

(defn do-run-source
  [store {:keys [db]} [id]]
  (timbre/infof "running: %s" id)
  {:http-xhrio
   (e/post-request-auth
    [:api-run-rate-source {:id id}]
    store
    (:token db)
    [::do-run-source-success]
    [::do-run-source-failed]
    {})})

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::item-map)
    (st/reg-sub ::item item-sub)
    (st/reg-sub ::items items-sub)
    (st/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))
    (st/reg-event-fx ::do-fetch-index-success (partial do-fetch-index-success store))
    (st/reg-event-fx ::do-fetch-index-failed (partial do-fetch-index-failed))
    (st/reg-event-fx ::do-fetch-index (partial do-fetch-index store))
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit (partial do-submit store))
    (st/reg-event-fx ::do-delete-record-failed (partial do-delete-record-failed store))
    (st/reg-event-fx ::do-delete-record-success (partial do-delete-record-success store))
    (st/reg-event-fx ::do-delete-record (partial do-delete-record store))
    (st/reg-event-fx ::do-run-source-failed (partial do-run-source-failed store))
    (st/reg-event-fx ::do-run-source-success (partial do-run-source-success store))
    (st/reg-event-fx ::do-run-source (partial do-run-source store)))
  store)
