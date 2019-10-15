(ns dinsro.events.accounts
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            ;; [dinsro.components.accounts :as c.accounts]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::accounts ::accounts)

(rf/reg-sub ::do-submit-loading ::do-submit-loading)

(rf/reg-event-fx
 ::do-submit-succeeded
 (fn-traced
  [_ data]
  (timbre/info "Submit success" data)
  {:dispatch [:dinsro.components.accounts/do-fetch-accounts]}))

(rf/reg-event-fx
 ::do-submit-failed
 (fn-traced
  [_ [_ response]]
  (timbre/info "Submit failed" response)))

(rf/reg-event-fx
 ::do-delete-account-success
 (fn [_ _]
   (timbre/info "delete account success")
   {:dispatch [::do-fetch-accounts]}))

(rf/reg-event-fx
 ::do-delete-account-failed
 (fn [_ _]
   (timbre/info "delete account failed")))

(rf/reg-event-db
 ::do-fetch-accounts-success
 (fn [db [_ {:keys [items]}]]
   (timbre/info "fetch accounts success" items)
   (assoc db ::accounts items)))

(rf/reg-event-fx
 ::do-fetch-accounts-failed
 (fn [_ _]
   (timbre/info "fetch accounts failed")))

(rf/reg-event-fx
 ::do-submit
 (fn-traced
  [_ [_ data]]
  {:http-xhrio
   {:method :post
    :uri "/api/v1/accounts"
    :params data
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success [::do-submit-succeeded]
    :on-failure [::do-submit-failed]}}))

(rf/reg-event-fx
 ::do-fetch-accounts
 (fn [_ _]
   {:http-xhrio
    {:uri "/api/v1/accounts"
     :method :get
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-fetch-accounts-success]
     :on-failure      [::do-fetch-accounts-failed]}}))

(rf/reg-event-fx
 ::do-delete-account
 (fn [_ [_ id]]
   {:http-xhrio
    {:uri (str "/api/v1/accounts/" id)
     :method :delete
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-delete-account-success]
     :on-failure      [::do-delete-account-failed]}}))
