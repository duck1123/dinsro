(ns dinsro.events.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec.rate-sources :as s.rate-sources]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(s/def ::item ::s.rate-sources/item)

;; Item Map

(s/def ::item-map (s/map-of :db/id ::item))
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

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [{:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::do-fetch-index-state :loaded))})

(defn do-fetch-index-failed
  [{:keys [db]} _]
  (timbre/info "fetch records failed")
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn do-fetch-index
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :loading)
   :http-xhrio
   (e/fetch-request [:api-index-rate-sources]
                    [::do-fetch-index-success]
                    [::do-fetch-index-failed])})

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
   (e/post-request [:api-index-rate-sources]
                   [::do-submit-success]
                   [::do-submit-failed]
                   data)})

(kf/reg-event-fx ::do-submit-failed  do-submit-failed)
(kf/reg-event-fx ::do-submit-success do-submit-success)
(kf/reg-event-fx ::do-submit         do-submit)

;; Delete

(defn do-delete-record-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record
  [_ [item]]
  {:http-xhrio
   (e/delete-request [:api-show-rate-source {:id (:db/id item)}]
                     [::do-delete-record-success]
                     [::do-delete-record-failed])})

(kf/reg-event-fx ::do-delete-record-failed  do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record         do-delete-record)


(defn do-run-source-failed
  [_ _]
  {})

(defn do-run-source-success
  [_ _]
  {})

(defn do-run-source
  [_ [id]]
  (timbre/infof "running: %s" id)
  {:http-xhrio
   (e/post-request [:api-run-rate-source {:id id}]
                   [::do-run-source-success]
                   [::do-run-source-failed]
                   {})})

(comment
  (kf/path-for [:api-run-rate-source {:id 1}]))

(kf/reg-event-fx ::do-run-source-failed  do-run-source-failed)
(kf/reg-event-fx ::do-run-source-success do-run-source-success)
(kf/reg-event-fx ::do-run-source         do-run-source)
