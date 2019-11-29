(ns dinsro.events.accounts
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items             (fn [db _] (get db ::items             [])))
(rf/reg-sub ::do-submit-loading (fn [db _] (get db ::do-submit-loading false)))

(s/def ::item (s/nilable ::s.accounts/item))
(s/def ::items (s/coll-of ::s.accounts/item))

(comment
  (gen/generate (s/gen ::items))
  )

(defn sub-item
  [items [_ target-item]]
  (first (filter #(= (:id %) (:db/id target-item)) items)))

(defn-spec items-by-user (s/coll-of ::s.accounts/item)
  [db any? event any?]
  (let [[_ id] event]
    (filter #(= id (get-in % [::s.accounts/user :db/id])) (::items db))))

(rf/reg-sub ::item :<- [::items] sub-item)
(rf/reg-sub ::items-by-user items-by-user)

;; Create

(defn do-submit-success
  [_ data]
  (timbre/info "Submit success" data)
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ [response]]
  (timbre/info "Submit failed" (get-in response [:parse-error :status-text])))

(defn do-submit
  [{:keys [db]} [data]]
  {:db (assoc db ::do-submit-loading true)
   :http-xhrio
   {:method          :post
    :uri             (kf/path-for [:api-index-accounts])
    :params          data
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-submit-success]
    :on-failure      [::do-submit-failed]}})

(kf/reg-event-fx ::do-submit-success   do-submit-success)
(kf/reg-event-fx ::do-submit-failed    do-submit-failed)
(kf/reg-event-fx ::do-submit           do-submit)

;; Delete

(kf/reg-event-fx
 ::do-delete-account-success
 (fn-traced [_ _]
   (timbre/info "delete account success")
   {:dispatch [::do-fetch-index]}))

(kf/reg-event-fx
 ::do-delete-account-failed
 (fn-traced [_ _]
   (timbre/info "delete account failed")))

(kf/reg-event-fx
 ::do-delete-account
 (fn-traced [_ [id]]
   {:http-xhrio
    {:uri             (kf/path-for [:api-show-account {:id id}])
     :method          :delete
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-delete-account-success]
     :on-failure      [::do-delete-account-failed]}}))

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [db [{:keys [items]}]]
  (timbre/info "fetch records success" items)
  (-> db
      (assoc ::items items)
      (assoc ::do-fetch-index-state :loaded)))

(defn do-fetch-index-failed
  [_ _]
  (timbre/info "fetch records failed"))

(defn do-fetch-index
  [_ _]
  {:http-xhrio
   {:uri             (kf/path-for [:api-index-accounts])
    :method          :get
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}})

(kf/reg-event-db ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
