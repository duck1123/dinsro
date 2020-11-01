(ns dinsro.events.rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.events.utils :as eu :include-macros true]
   [dinsro.specs.events.rates :as s.e.rates]
   [dinsro.specs.rates :as s.rates]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(s/def ::item ::s.rates/item)

(eu/declare-model 'dinsro.events.rates)
(eu/declare-fetch-index-method 'dinsro.events.rates)
(eu/declare-fetch-record-method 'dinsro.events.rates)
(eu/declare-delete-record-method 'dinsro.events.rates)

(defn items-by-currency
  "Subscription handler: Index items by currency"
  [{:keys [::item-map]} [_ {:keys [db/id]}]]
  (filter #(= (get-in % [::s.rates/currency :db/id]) id) (vals item-map)))

(s/fdef items-by-currency
  :args (s/cat :db (s/keys :req [::item-map])
               :event (s/cat :keyword keyword? :currency ::item))
  :ret ::items)

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
    (eu/register-model-store 'dinsro.events.rates)
    (eu/register-fetch-index-method 'dinsro.events.rates [:api-index-rates])
    (eu/register-fetch-record-method 'dinsro.events.rates [:api-show-rate])
    (eu/register-delete-record-method 'dinsro.events.currencies [:api-delete-rate])
    (st/reg-sub ::items-by-currency items-by-currency)
    (st/reg-sub ::rate-feed rate-feed-sub)
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit (partial do-submit store))
    (st/reg-event-fx ::do-fetch-rate-feed-by-currency-success (partial do-fetch-rate-feed-by-currency-success store))
    (st/reg-event-fx ::do-fetch-rate-feed-by-currency-failure (partial do-fetch-rate-feed-by-currency-failure store))
    (st/reg-event-fx ::do-fetch-rate-feed-by-currency (partial do-fetch-rate-feed-by-currency store))
    (st/reg-event-fx ::add-record (partial add-record store)))
  store)
