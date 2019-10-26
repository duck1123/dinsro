(ns dinsro.events.accounts
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items             (fn [db _] (get db ::items             [])))
(rf/reg-sub ::do-submit-loading (fn [db _] (get db ::do-submit-loading false)))

(rf/reg-sub
 ::item
 :<- [::items]
 (fn-traced [items [_ target-item]]
   (first (filter #(= (:id %) (:id target-item)) items))))

(kf/reg-event-fx
 ::do-submit-succeeded
 (fn-traced [_ data]
  (timbre/info "Submit success" data)
  {:dispatch [::do-fetch-accounts]}))

(kf/reg-event-fx
 ::do-submit-failed
 (fn-traced [[_ response]]
  (timbre/info "Submit failed" response)))

(kf/reg-event-fx
 ::do-delete-account-success
 (fn-traced [_ _]
   (timbre/info "delete account success")
   {:dispatch [::do-fetch-accounts]}))

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
 (fn-traced [_ [_ id]]
   {:http-xhrio
    {:uri             (kf/path-for [:api-show-account {:id id}])
     :method          :delete
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-delete-account-success]
     :on-failure      [::do-delete-account-failed]}}))
