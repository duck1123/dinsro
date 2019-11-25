(ns dinsro.events.accounts
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items             (fn [db _] (get db ::items             [])))
(rf/reg-sub ::do-submit-loading (fn [db _] (get db ::do-submit-loading false)))

(s/def ::item (s/keys ))
(s/def ::items (s/coll-of ::item))

(comment
  (gen/generate (s/gen ::items))
  )

(defn sub-item
  [items [_ target-item]]
  (first (filter #(= (:id %) (:db/id target-item)) items)))

(rf/reg-sub ::item :<- [::items] sub-item)

(defn do-submit-succeeded
  [_ data]
  (timbre/info "Submit success" data)
  {:dispatch [::do-fetch-index]})

(kf/reg-event-fx ::do-submit-succeeded do-submit-succeeded)

(kf/reg-event-fx
 ::do-submit-failed
 (fn-traced [_ [response]]
  (timbre/info "Submit failed" (get-in response [:parse-error :status-text]))))

(kf/reg-event-fx
 ::do-delete-account-success
 (fn-traced [_ _]
   (timbre/info "delete account success")
   {:dispatch [::do-fetch-index]}))

(kf/reg-event-fx
 ::do-delete-account-failed
 (fn-traced [_ _]
   (timbre/info "delete account failed")))

(kf/reg-event-db
 ::do-fetch-index-success
 (fn-traced [db [{:keys [items]}]]
   (timbre/info "fetch records success" items)
   (assoc db ::items items)))

(kf/reg-event-fx
 ::do-fetch-index-failed
 (fn-traced [_ _]
   (timbre/info "fetch records failed")))

(kf/reg-event-fx
 ::do-submit
 (fn-traced [{:keys [db]} [data]]
   {:db (assoc db ::do-submit-loading true)
    :http-xhrio
    {:method          :post
     :uri             (kf/path-for [:api-index-accounts])
     :params          data
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-submit-succeeded]
     :on-failure      [::do-submit-failed]}}))

(kf/reg-event-fx
 ::do-fetch-index
 (fn-traced [_ _]
   {:http-xhrio
    {:uri             (kf/path-for [:api-index-accounts])
     :method          :get
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-fetch-index-success]
     :on-failure      [::do-fetch-index-failed]}}))

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
