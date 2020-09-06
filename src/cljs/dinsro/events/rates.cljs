(ns dinsro.events.rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec.events.rates :as s.e.rates]
   [dinsro.spec.rates :as s.rates]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(s/def ::item ::s.rates/item)

;; Item Map

(s/def ::item-map (s/map-of :db/id ::item))
(rfu/reg-basic-sub ::item-map)
(def item-map ::item-map)

;; Items

(s/def ::items (s/coll-of ::item))

(defn items-sub
  "Subscription handler: Index all items"
  [{:keys [::item-map]} _]
  (reverse
   (sort-by ::s.rates/date (vals item-map))))

(s/def ::item-sub-cofx (s/keys :req [::item-map]))

(s/fdef items-sub
  :args (s/cat :item-map ::items-sub-cofx
               :event (s/cat :kw keyword?))
  :ret ::items)

(rf/reg-sub ::items items-sub)

;; Items by Currency

(defn items-by-currency
  "Subscription handler: Index items by currency"
  [{:keys [::item-map]} [_ {:keys [db/id]}]]
  (filter #(= (get-in % [::s.rates/currency :db/id]) id) (vals item-map)))

(s/fdef items-by-currency
  :args (s/cat :db (s/keys :req [::item-map])
               :event (s/cat :keyword keyword? :currency ::item))
  :ret ::items)

(rf/reg-sub ::items-by-currency items-by-currency)

;; Item

(defn item-sub
  "Subscription handler: Lookup an item from the item map by id"
  [{:keys [::item-map]} [_ id]]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :item-map ::item-map
               :event (s/cat :kw keyword? :id :db/id))
  :ret ::item)

(rf/reg-sub ::item item-sub)

;; Read

(s/def ::do-fetch-record-state keyword?)
(rf/reg-sub ::do-fetch-record-state (fn [db _] (get db ::do-fetch-record-state :invalid)))

(defn do-fetch-record-success
  [{:keys [db]} [{:keys [item]}]]
  (let [item (update item ::s.rates/date tick/instant)]
    {:db (-> db
             (assoc ::do-fetch-record-state :loaded)
             (assoc ::item item)
             (assoc-in [::item-map (:db/id item)] item))}))

(defn do-fetch-record-unauthorized
  [{:keys [db]} _event]
  (let [match (:kee-frame/route db)]
    {:db (assoc db :return-to match)
     :navigate-to [:login-page]}))

(defn do-fetch-record-failed
  [{:keys [db]} [{:keys [status] :as request}]]
  (if (= status/forbidden status)
    {:dispatch [::do-fetch-record-unauthorized request]}
    {:db (assoc db ::do-fetch-record-state :failed)}))

(s/def ::do-fetch-record-failed-event
  (s/cat :id       :db/id
         :success  (s/? (s/tuple #{:foo}))
         :failure  (s/? (s/tuple #{:failure}))))
(def do-fetch-record-failed-event ::do-fetch-record-failed-event)

(s/fdef do-fetch-record-failed
  :ret (s/keys))

(defn do-fetch-record
  [{:keys [db]} [id success failure]]
  {:db (assoc db ::do-fetch-record-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-show-rate {:id id}]
    (:token db)
    (or success [::do-fetch-record-success])
    (or failure [::do-fetch-record-failed]))})

(s/def ::do-fetch-record-event
  (s/cat :id       :db/id
         :success  (s/? (s/tuple #{:foo}))
         :failure  (s/? (s/tuple #{:failure}))))
(def do-fetch-record-event ::do-fetch-record-event)

(s/fdef do-fetch-record
  :args (s/cat :cofx (s/keys)
               :event ::do-fetch-record-event)
  :ret (s/keys))

(kf/reg-event-fx ::do-fetch-record-success       do-fetch-record-success)
(kf/reg-event-fx ::do-fetch-record-failed        do-fetch-record-failed)
(kf/reg-event-fx ::do-fetch-record-unauthorized  do-fetch-record-unauthorized)
(kf/reg-event-fx ::do-fetch-record               do-fetch-record)

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
   (e/fetch-request-auth
    [:api-index-rates]
    (:token db)
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
  [{:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-rates]
    (:token db)
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
  [{:keys [db]} [item]]
  (let [id (:db/id item)]
    {:http-xhrio
     (e/delete-request-auth
      [:api-show-rate {:id id}]
      (:token db)
      [::do-delete-record-success]
      [::do-delete-record-failed])}))

(s/fdef do-delete-record
  :args (s/cat :cofx ::s.e.rates/do-delete-record-cofx
               :event ::s.e.rates/do-delete-record-event)
  :ret (s/keys))

(kf/reg-event-fx ::do-delete-record-failed  do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record         do-delete-record)


(defn do-fetch-rate-feed-by-currency-success
  [{:keys [db]} [id {:keys [items]}]]
  {:db (assoc-in db [::rate-feed id] items)})

(defn do-fetch-rate-feed-by-currency-failure
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-fetch-rate-feed-by-currency
  [{:keys [db]} [id]]
  {:http-xhrio
   (e/fetch-request-auth
    [:api-rate-feed {:id id}]
    (:token db)
    [::do-fetch-rate-feed-by-currency-success id]
    [::do-fetch-rate-feed-by-currency-failure id])})

(kf/reg-event-fx ::do-fetch-rate-feed-by-currency-success do-fetch-rate-feed-by-currency-success)
(kf/reg-event-fx ::do-fetch-rate-feed-by-currency-failure do-fetch-rate-feed-by-currency-failure)
(kf/reg-event-fx ::do-fetch-rate-feed-by-currency do-fetch-rate-feed-by-currency)

(defn rate-feed-sub
  [db [_ id]]
  (get-in db [::rate-feed id]))

(rf/reg-sub ::rate-feed rate-feed-sub)

(defn add-record
  "Handler for rates created"
  [{:keys [db]} [id response]]
  (if response
    (if-let [rate (:item response)]
      (do (timbre/infof "Adding new rate: %s" rate)
          (let [currency-id (get-in rate [::s.rates/currency :db/id])
                time (.getTime (tick/inst (::s.rates/date rate)))
                rate-value (::s.rates/rate rate)
                rate-item [time rate-value]]
            {:db (update-in db [::rate-feed currency-id] concat [rate-item])}))
      (do
        (timbre/error "Did not receive a valid rate")
        {}))
    {:dispatch [::do-fetch-record id [::add-record id]]}))

(s/fdef add-record
  :args (s/cat :cofx ::s.e.rates/add-record-cofx
               :event ::s.e.rates/add-record-event)
  :ret (s/keys))

(kf/reg-event-fx ::add-record add-record)
