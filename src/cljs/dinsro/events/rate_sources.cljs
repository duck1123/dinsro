(ns dinsro.events.rate-sources
  (:require [ajax.core :as ajax]
            [cljc.java-time.instant :as instant]
            [clojure.spec.alpha :as s]
            [dinsro.events :as e]
            [dinsro.spec.events.rate-sources :as s.e.rate-sources]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]))

(s/def ::items                   (s/coll-of ::s.rate-sources/item))
(def items ::items)
(rf/reg-sub ::items              (fn [db _] (get db ::items [])))

;; (s/def ::items-by-currency-event (s/cat :keyword keyword? :currency ::s.currencies/item))

;; (defn-spec items-by-currency ::items
;;   [items ::items [_ {:keys [db/id]}] ::items-by-currency-event]
;;   (filter #(= (get-in % [::s.rates/currency :db/id]) id) items))

;; (rf/reg-sub ::items-by-currency :<- [::items] items-by-currency)

(rf/reg-sub
 ::item
 :<- [::items]
 (fn [items [_ id]]
   (first (filter #(= (:id %) id) items))))
(def item ::item)
;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [cofx event]
  (let [{:keys [db]} cofx
        [{:keys [items]}] event]
    {:db (-> db
             (assoc ::items items)
             (assoc ::do-fetch-index-state :loaded))}))

(defn do-fetch-index-failed
  [{:keys [db]} _]
  (timbre/info "fetch records failed")
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn do-fetch-index
  [{:keys [db]} _]
  ;; {:db (assoc db ::items (ds/gen-key ::items))}
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

(defn-spec do-submit (s/keys)
  [{:keys [db]} any?
   [data] any?]
  {:http-xhrio
   (e/post-request [:api-index-rate-sources]
                   [::do-submit-success]
                   [::do-submit-failed]
                   data)})

(kf/reg-event-fx ::do-submit-failed  do-submit-failed)
(kf/reg-event-fx ::do-submit-success do-submit-success)
(kf/reg-event-fx ::do-submit         do-submit)

;; Delete

(defn-spec do-delete-record-success (s/keys)
  [cofx ::s.e.rate-sources/do-delete-record-success-cofx _ any?]
  {:dispatch [::do-fetch-index]})

(defn-spec do-delete-record-failed (s/keys)
  [cofx ::s.e.rate-sources/do-delete-record-failed-cofx _ any?]
  (timbre/error "Delete record failed")
  {:dispatch [::do-fetch-index]})

(defn-spec do-delete-record (s/keys)
  [cofx ::s.e.rate-sources/do-delete-record-cofx
   [item] ::s.e.rate-sources/do-delete-record-event]
  {:http-xhrio
   (e/delete-request [:api-show-rate-source {:id (:d/id item)}]
                     [::do-delete-record-success]
                     [::do-delete-record-failed])})

(kf/reg-event-fx ::do-delete-record-failed  do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record         do-delete-record)


(defn do-run-source-failed
  [_ _]
  {}
  )

(defn do-run-source-success
  [_ _]
  {}
  )

(defn do-run-source
  [_ [id]]
  (timbre/infof "running: %s" id)
  {}
  )

(kf/reg-event-fx ::do-run-source-failed  do-run-source-failed)
(kf/reg-event-fx ::do-run-source-success do-run-source-success)
(kf/reg-event-fx ::do-run-source         do-run-source)
