(ns dinsro.events.transactions
  (:require [clojure.spec.alpha :as s]
            [dinsro.events :as e]
            [dinsro.spec.events.transactions :as s.e.transactions]
            [dinsro.spec.transactions :as s.transactions]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]))

(def example-transaction
  {:db/id 1
   ::s.transactions/value 130000
   ::s.transactions/date (tick/instant)
   ::s.transactions/currency {:db/id 53}
   ::s.transactions/account {:db/id 12}})

(s/def ::items (s/coll-of ::s.transactions/item))
(def items ::items)
(rfu/reg-basic-sub ::items)

(defn-spec items-by-account ::items
  [items ::items
   event ::s.e.transactions/items-by-account-event]
  (let [[_ id] event]
    (filter #(= (get-in % [::s.transactions/account :db/id]) id) items)))

(defn-spec items-by-currency ::items
  [items ::items
   event ::s.e.transactions/items-by-currency-event]
  (let [[_ id] event]
    (filter #(= (get-in % [::s.transactions/currency :db/id]) id) items)))

(rf/reg-sub ::items-by-account :<- [::items] items-by-account)
(rf/reg-sub ::items-by-currency :<- [::items] items-by-currency)

;; FIXME: This will have to read across all linked accounts
(rfu/reg-basic-sub ::items-by-user ::items)

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [{:keys [db]} [{:keys [items]}]]
  (let [items (map
               (fn [item] (update item ::s.transactions/date tick/instant))
               items)]
    {:db (-> db
             (assoc ::items items)
             (assoc ::do-fetch-index-state :loaded))}))

(defn do-fetch-index-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn-spec do-fetch-index ::s.e.transactions/do-fetch-index-response
  [_ ::s.e.transactions/do-fetch-index-cofx
   _ ::s.e.transactions/do-fetch-index-event]
  {:http-xhrio
   (e/fetch-request
    [:api-index-transactions]
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)

;; Submit

(s/def ::do-submit-state keyword?)
(rf/reg-sub ::do-submit-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-submit-success
  [{:keys [db]} _]
  {:db (assoc db ::do-submit-state :success)
   :dispatch [::do-fetch-index]})

(defn do-submit-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-submit-state :failed)})

(defn do-submit
  [_ [data]]
  {:http-xhrio
   (e/post-request
    [:api-index-transactions]
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
  (let [id (:db/id item)]
    {:http-xhrio
     (e/delete-request
      [:api-show-transaction {:id id}]
      [::do-delete-record-success]
      [::do-delete-record-failed])}))

(s/fdef do-delete-record
  :args (s/cat :cofx ::e.transactions/do-delete-record-cofx
               :event  ::e.transactions/do-delete-record-event)
  :ret (s/keys))

(kf/reg-event-fx ::do-delete-record-failed  do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record         do-delete-record)
