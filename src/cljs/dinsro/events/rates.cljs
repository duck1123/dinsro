(ns dinsro.events.rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec.events.rates :as s.e.rates]
   [dinsro.spec.rates :as s.rates]
   [dinsro.store :as st]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(s/def ::item ::s.rates/item)

;; Item Map

(s/def ::item-map (s/map-of :db/id ::item))
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

;; Items by Currency

(defn items-by-currency
  "Subscription handler: Index items by currency"
  [{:keys [::item-map]} [_ {:keys [db/id]}]]
  (filter #(= (get-in % [::s.rates/currency :db/id]) id) (vals item-map)))

(s/fdef items-by-currency
  :args (s/cat :db (s/keys :req [::item-map])
               :event (s/cat :keyword keyword? :currency ::item))
  :ret ::items)

;; Item

(defn item-sub
  "Subscription handler: Lookup an item from the item map by id"
  [{:keys [::item-map]} [_ id]]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :item-map ::item-map
               :event (s/cat :kw keyword? :id :db/id))
  :ret ::item)

;; Read

(s/def ::do-fetch-record-state keyword?)

(defn do-fetch-record-success
  [_store {:keys [db]} [{:keys [item]}]]
  (let [item (update item ::s.rates/date tick/instant)]
    {:db (-> db
             (assoc ::do-fetch-record-state :loaded)
             (assoc ::item item)
             (assoc-in [::item-map (:db/id item)] item))}))

(defn do-fetch-record-unauthorized
  [_store {:keys [db]} _event]
  (let [match (:kee-frame/route db)]
    {:db (assoc db :return-to match)
     :navigate-to [:login-page]}))

(defn do-fetch-record-failed
  [_store {:keys [db]} [{:keys [status] :as request}]]
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
  [store {:keys [db]} [id success failure]]
  {:db (assoc db ::do-fetch-record-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-show-rate {:id id}]
    store
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

;; Index

(s/def ::do-fetch-index-state keyword?)

(defn do-fetch-index-success
  [_store {:keys [db]} [{:keys [items]}]]
  (let [items (map (fn [item] (update item ::s.rates/date tick/instant)) items)]
    {:db (-> db
             (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
             (assoc ::do-fetch-index-state :loaded))}))

(defn do-fetch-index-failed
  [_store {:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn do-fetch-index
  [store {:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-index-rates]
    store
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

;; Submit

(defn do-submit-success
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-submit
  [store {:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-rates]
    store
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

(s/fdef do-submit
  :ret (s/keys))

;; Delete

(defn do-delete-record-success
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(s/fdef do-delete-record-success
  :args (s/cat :cofx ::s.e.rates/do-delete-record-success-cofx
               :event ::s.e.rates/do-delete-record-success-event)
  :ret ::s.e.rates/do-delete-record-success-response)

(defn do-delete-record-failed
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(s/fdef do-delete-record-failed
  :args (s/cat :cofx ::s.e.rates/do-delete-record-failed-cofx
               :event any?)
  :ret (s/keys))

(defn do-delete-record
  [store {:keys [db]} [item]]
  (let [id (:db/id item)]
    {:http-xhrio
     (e/delete-request-auth
      [:api-show-rate {:id id}]
      store
      (:token db)
      [::do-delete-record-success]
      [::do-delete-record-failed])}))

(s/fdef do-delete-record
  :args (s/cat :cofx ::s.e.rates/do-delete-record-cofx
               :event ::s.e.rates/do-delete-record-event)
  :ret (s/keys))

(defn do-fetch-rate-feed-by-currency-success
  [_store {:keys [db]} [id {:keys [items]}]]
  {:db (assoc-in db [::rate-feed id] items)})

(defn do-fetch-rate-feed-by-currency-failure
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-fetch-rate-feed-by-currency
  [store {:keys [db]} [id]]
  {:http-xhrio
   (e/fetch-request-auth
    [:api-rate-feed {:id id}]
    store
    (:token db)
    [::do-fetch-rate-feed-by-currency-success id]
    [::do-fetch-rate-feed-by-currency-failure id])})

(defn rate-feed-sub
  [db [_ id]]
  (get-in db [::rate-feed id]))

(defn add-record
  "Handler for rates created"
  [_store {:keys [db]} [id response]]
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

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::item-map)
    (st/reg-sub ::item item-sub)
    (st/reg-sub ::items items-sub)
    (st/reg-sub ::items-by-currency items-by-currency)
    (st/reg-sub ::do-fetch-record-state (fn [db _] (get db ::do-fetch-record-state :invalid)))
    (st/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))
    (st/reg-sub ::rate-feed rate-feed-sub)
    (st/reg-event-fx ::do-fetch-record-success (partial do-fetch-record-success store))
    (st/reg-event-fx ::do-fetch-record-failed (partial do-fetch-record-failed store))
    (st/reg-event-fx ::do-fetch-record-unauthorized (partial do-fetch-record-unauthorized store))
    (st/reg-event-fx ::do-fetch-record (partial do-fetch-record store))
    (st/reg-event-fx ::do-fetch-index-success (partial do-fetch-index-success store))
    (st/reg-event-fx ::do-fetch-index-failed (partial do-fetch-index-failed store))
    (st/reg-event-fx ::do-fetch-index (partial do-fetch-index store))
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit (partial do-submit store store))
    (st/reg-event-fx ::do-delete-record-failed (partial do-delete-record-failed store))
    (st/reg-event-fx ::do-delete-record-success (partial do-delete-record-success store))
    (st/reg-event-fx ::do-delete-record (partial do-delete-record store))
    (st/reg-event-fx ::do-fetch-rate-feed-by-currency-success (partial do-fetch-rate-feed-by-currency-success store))
    (st/reg-event-fx ::do-fetch-rate-feed-by-currency-failure (partial do-fetch-rate-feed-by-currency-failure store))
    (st/reg-event-fx ::do-fetch-rate-feed-by-currency (partial do-fetch-rate-feed-by-currency store))
    (st/reg-event-fx ::add-record (partial add-record store)))
  store)
