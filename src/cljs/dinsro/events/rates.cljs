(ns dinsro.events.rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.events :as e]
   [dinsro.spec.events.rates :as s.e.rates]
   [dinsro.spec.rates :as s.rates]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

;; Items

(s/def ::items (s/coll-of ::s.rates/item))
(rf/reg-sub ::items (fn [db _] (get db ::items [])))

;; Item Map

(s/def ::item-map (s/map-of :db/id ::s.rates/item))
(rfu/reg-basic-sub ::item-map)
(def item-map ::item-map)

;; Items by Currency

(s/def ::items-by-currency-event (s/cat :keyword keyword? :currency ::s.currencies/item))

(defn items-by-currency
  [items [_ {:keys [db/id]}]]
  (filter #(= (get-in % [::s.rates/currency :db/id]) id) items))

(s/fdef items-by-currency
  :args (s/cat :items ::items :event ::items-by-currency-event)
  :ret ::items)

(rf/reg-sub ::items-by-currency :<- [::items] items-by-currency)

;; Item

(rf/reg-sub
 ::item
 :<- [::items]
 (fn [items [_ id]]
   (first (filter #(= (:id %) id) items))))

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [{:keys [db]} [{:keys [items]}]]
  (let [items (map (fn [item] (update item ::s.rates/date tick/instant)) items)]
    {:db (-> db
             (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
             (assoc ::do-fetch-index-state :loaded))}))

(defn do-fetch-index-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn do-fetch-index
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :loading)
   :http-xhrio
   (e/fetch-request [:api-index-rates]
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
   (e/post-request [:api-index-rates]
                   [::do-submit-success]
                   [::do-submit-failed]
                   data)})

(s/fdef do-submit
  :ret (s/keys))

(kf/reg-event-fx ::do-submit-failed  do-submit-failed)
(kf/reg-event-fx ::do-submit-success do-submit-success)
(kf/reg-event-fx ::do-submit         do-submit)

;; Delete

(defn do-delete-record-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(s/fdef do-delete-record-success
  :args (s/cat :cofx ::s.e.rates/do-delete-record-success-cofx
               :event ::s.e.rates/do-delete-record-success-event)
  :ret ::s.e.rates/do-delete-record-success-response)

(defn do-delete-record-failed
  [_ _]
  {:dispatch [::do-fetch-index]})

(s/fdef do-delete-record-failed
  :args (s/cat :cofx ::s.e.rates/do-delete-record-failed-cofx
               :event any?)
  :ret (s/keys))

(defn do-delete-record
  [_ [item]]
  (let [id (:db/id item)]
    {:http-xhrio
     (e/delete-request [:api-show-rate {:id id}]
                       [::do-delete-record-success]
                       [::do-delete-record-failed])}))

(s/fdef do-delete-record
  :args (s/cat :cofx ::s.e.rates/do-delete-record-cofx
               :event ::s.e.rates/do-delete-record-event)
  :ret (s/keys))

(kf/reg-event-fx ::do-delete-record-failed  do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record         do-delete-record)
