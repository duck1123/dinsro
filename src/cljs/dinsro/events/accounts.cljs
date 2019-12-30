(ns dinsro.events.accounts
  (:require [clojure.spec.alpha :as s]
            [dinsro.events :as e]
            [dinsro.spec :as ds]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.events.accounts :as s.e.accounts]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(s/def ::items (s/coll-of ::s.accounts/item))
(rfu/reg-basic-sub ::items)

(defn-spec sub-item (s/nilable ::s.accounts/item)
  [items ::items
   [_ id] ::s.e.accounts/sub-item-event]
  (first (filter #(= (:db/id %) id) items)))

(defn-spec items-by-user ::items
  [items ::items event any?]
  (let [[_ id] event]
    (filter #(= id (get-in % [::s.accounts/user :db/id])) items)))

(defn-spec items-by-currency ::items
  [items ::items event any?]
  (let [[_ item] event
        id (:db/id item)]
    (filter #(= id (get-in % [::s.accounts/currency :db/id])) items)))

(rf/reg-sub ::item :<- [::items] sub-item)
(def item ::item)

(rf/reg-sub ::items-by-user :<- [::items] items-by-user)
(rf/reg-sub ::items-by-currency :<- [::items] items-by-currency)

;; Create
(s/def ::do-submit-state ::ds/state)
(rfu/reg-basic-sub ::do-submit-state)

(defn do-submit-success
  [{:keys [db]} data]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [{:keys [db]} [response]]
  {})

(defn-spec do-submit ::s.e.accounts/do-submit-response
  [{:keys [db]} ::s.e.accounts/do-submit-response-cofx
   [data] ::s.e.accounts/do-submit-response-event]
  {:http-xhrio
   (e/post-request
    [:api-index-accounts]
    [::do-submit-success]
    [::do-submit-failed]
    data)})

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

(defn-spec do-fetch-index-failed ::s.e.accounts/do-fetch-index-failed-response
  [{:keys [db]} ::s.e.accounts/do-fetch-index-failed-cofx
   _ ::s.e.accounts/do-fetch-index-failed-event]
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn-spec do-fetch-index ::s.e.accounts/do-fetch-index-response
  [{:keys [db]} ::s.e.accounts/do-fetch-index-cofx
   _ ::s.e.accounts/do-fetch-index-event]
  {:db (assoc db ::do-fetch-index-state :loading)
   :http-xhrio
   (e/fetch-request
    [:api-index-accounts]
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
