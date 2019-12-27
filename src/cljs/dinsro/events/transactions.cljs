(ns dinsro.events.transactions
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [dinsro.events.transactions :as e.transactions]
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
  [items ::e.transactions/items
   event ::e.transactions/items-by-account-event]
  (let [[_ id] event]
    (filter #(= (get-in % [::s.transactions/account :db/id]) id) items)))

(defn-spec items-by-currency ::items
  [items ::e.transactions/items
   event ::e.transactions/items-by-currency-event]
  (let [[_ id] event]
    (filter #(= (get-in % [::s.transactions/currency :db/id]) id) items)))

(rf/reg-sub ::items-by-account :<- [::items] items-by-account)
(rf/reg-sub ::items-by-currency :<- [::items] items-by-currency)

;; FIXME: This will have to read across all linked accounts
(rfu/reg-basic-sub ::items-by-user ::items)

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
  [_ _]
  {})

(defn do-fetch-index
  [_ _]
  {:http-xhrio
   {:method          :get
    :uri             (kf/path-for [:api-index-transactions])
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}})

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
   {:method          :post
    :uri             (kf/path-for [:api-index-transactions])
    :params          data
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-submit-success]
    :on-failure      [::do-submit-failed]}})

(kf/reg-event-fx ::do-submit-failed  do-submit-failed)
(kf/reg-event-fx ::do-submit-success do-submit-success)
(kf/reg-event-fx ::do-submit         do-submit)

;; Delete

(defn do-delete-record-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_ _]
  (timbre/error "Delete record failed")
  {:dispatch [::do-fetch-index]})

(defn do-delete-record
  [_ [item]]
  (let [id (:db/id item)]
    {:http-xhrio
     {:uri             (kf/path-for [:api-show-transaction {:id id}])
      :method          :delete
      :format          (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})
      :on-success      [::do-delete-record-success]
      :on-failure      [::do-delete-record-failed]}}))

(s/fdef do-delete-record
  :args (s/cat :cofx ::e.transactions/do-delete-record-cofx
               :event  ::e.transactions/do-delete-record-event)
  :ret (s/keys))

(kf/reg-event-fx ::do-delete-record-failed  do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record         do-delete-record)
