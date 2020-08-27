(ns dinsro.events.accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.events.accounts :as s.e.accounts]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(s/def ::item ::s.accounts/item)

;; Item Map

(s/def ::item-map (s/map-of ::ds/id ::s.accounts/item))
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

(s/def ::item-sub-event (s/tuple keyword? :db/id))

(defn item-sub
  "Subscription handler: Lookup an item from the item map by id"
  [{:keys [::item-map]} [_ id]]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :db (s/keys :req [::item-map])
               :event ::item-sub-event)
  :ret (s/nilable ::item))

;; Items by User

(defn items-by-user
  [{:keys [::item-map]} [_ id]]
  (filter #(= id (get-in % [::s.accounts/user :db/id])) (vals item-map)))

(s/fdef items-by-user
  :args (s/cat :db (s/keys :req [::item-map])
               :event (s/cat :kw keyword?
                             :id :db/id))
  :ret ::items)

;; Items by Currency

(defn items-by-currency
  [{:keys [::item-map]} [_ item]]
  (let [id (:db/id item)]
    (filter #(= id (get-in % [::s.accounts/currency :db/id])) (vals item-map))))

(s/fdef items-by-currency
  :args (s/cat :db (s/keys :req [::item-map])
               :event any?)
  :ret ::items)

;; Create

(s/def ::do-submit-state ::ds/state)

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
    [:api-index-accounts]
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

(s/fdef do-submit
  :args (s/cat :cofx ::s.e.accounts/do-submit-response-cofx
               :event ::s.e.accounts/do-submit-response-event)
  :ret ::s.e.accounts/do-submit-response)

;; Delete

(s/def ::do-delete-record-state ::ds/state)

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
    [:api-show-account {:id (:db/id item)}]
    (:token db)
    [::do-delete-record-success]
    [::do-delete-record-failed])})

;; Index

(s/def ::do-fetch-index-state ::ds/state)
(def do-fetch-index-state ::do-fetch-index-state)

(defn do-fetch-index-success
  [{:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::do-fetch-index-state :loaded))})

(defn do-fetch-index-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :failed)})

(s/fdef do-fetch-index-failed
  :args (s/cat :cofx ::s.e.accounts/do-fetch-index-failed-cofx
               :event ::s.e.accounts/do-fetch-index-failed-event)
  :ret ::s.e.accounts/do-fetch-index-failed-response)

(defn do-fetch-index
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-index-accounts]
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(s/fdef do-fetch-index
  :args (s/cat :cofx ::s.e.accounts/do-fetch-index-cofx
               :event ::s.e.accounts/do-fetch-index-event)
  :ret ::s.e.accounts/do-fetch-index-response)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::item-map)
    (st/reg-sub ::item item-sub)
    (st/reg-sub ::items items-sub)
    (st/reg-sub ::items-by-currency items-by-currency)
    (st/reg-sub ::items-by-user items-by-user)
    (st/reg-basic-sub ::do-submit-state)
    (st/reg-event-fx ::do-submit-success do-submit-success)
    (st/reg-event-fx ::do-submit-failed do-submit-failed)
    (st/reg-event-fx ::do-submit do-submit)
    (st/reg-basic-sub ::do-delete-record-state)
    (st/reg-event-fx ::do-delete-record-success do-delete-record-success)
    (st/reg-event-fx ::do-delete-record-failed do-delete-record-failed)
    (st/reg-event-fx ::do-delete-record do-delete-record)
    (st/reg-basic-sub ::do-fetch-index-state)
    (st/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
    (st/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
    (st/reg-event-fx ::do-fetch-index do-fetch-index))
  store)
