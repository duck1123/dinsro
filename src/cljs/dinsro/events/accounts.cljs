(ns dinsro.events.accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.events.accounts :as s.e.accounts]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(s/def ::items (s/coll-of ::s.accounts/item))
(rfu/reg-basic-sub ::items)

(defn sub-item
  [items [_ id]]
  (first (filter #(= (:db/id %) id) items)))

(s/fdef sub-item
  :args (s/cat :item ::items
               :event ::s.e.accounts/sub-item-event)
  :ret (s/nilable ::s.accounts/item))

(defn items-by-user
  [items [_ id]]
  (filter #(= id (get-in % [::s.accounts/user :db/id])) items))

(s/fdef items-by-user
  :args (s/cat :items ::items
               :event (s/cat :kw keyword?
                             :id :db/id))
  :ret ::items)

(defn items-by-currency
  [items [_ item]]
  (let [id (:db/id item)]
    (filter #(= id (get-in % [::s.accounts/currency :db/id])) items)))

(s/fdef items-by-currency
  :args (s/cat :items ::items
               :event any?)
  :ret ::items)

(rf/reg-sub ::item :<- [::items] sub-item)
(def item ::item)

(rf/reg-sub ::items-by-user :<- [::items] items-by-user)
(rf/reg-sub ::items-by-currency :<- [::items] items-by-currency)

;; Create
(s/def ::do-submit-state ::ds/state)
(rfu/reg-basic-sub ::do-submit-state)

(defn do-submit-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ _]
  {})

(defn do-submit
  [_ [data]]
  {:http-xhrio
   (e/post-request
    [:api-index-accounts]
    [::do-submit-success]
    [::do-submit-failed]
    data)})

(s/fdef do-submit
  :args (s/cat :cofx ::s.e.accounts/do-submit-response-cofx
               :event ::s.e.accounts/do-submit-response-event)
  :ret ::s.e.accounts/do-submit-response)

(kf/reg-event-fx ::do-submit-success   do-submit-success)
(kf/reg-event-fx ::do-submit-failed    do-submit-failed)
(kf/reg-event-fx ::do-submit           do-submit)

;; Delete
(s/def ::do-delete-record-state ::ds/state)
(rfu/reg-basic-sub ::do-delete-record-state)

(defn do-delete-record-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_ _]
  {})

(defn do-delete-record
  [_ [item]]
  {:http-xhrio
   (e/delete-request
    [:api-show-account {:id (:db/id item)}]
    [::do-delete-record-success]
    [::do-delete-record-failed])})

(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record-failed do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record do-delete-record)

;; Index

(s/def ::do-fetch-index-state ::ds/state)
(rfu/reg-basic-sub ::do-fetch-index-state)
(def do-fetch-index-state ::do-fetch-index-state)

(defn do-fetch-index-success
  [{:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (assoc ::items items)
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
   (e/fetch-request
    [:api-index-accounts]
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(s/fdef do-fetch-index
  :args (s/cat :cofx ::s.e.accounts/do-fetch-index-cofx
               :event ::s.e.accounts/do-fetch-index-event)
  :ret ::s.e.accounts/do-fetch-index-response)

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
