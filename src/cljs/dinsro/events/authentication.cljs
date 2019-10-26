(ns dinsro.events.authentication
  (:require [ajax.core :as ajax]
            [clojure.string :as string]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(kf/reg-event-db
 ::do-authenticate-success
 (fn-traced [db [{:keys [identity]}]]
   (assoc db :authenticated identity)))

(kf/reg-event-db
 ::do-authenticate-failure
 (fn-traced [db _]
   (-> db
       (assoc ::login-failed true)
       (assoc ::loading false))))

(kf/reg-event-db
 ::do-logout-success
 (fn-traced [db _]
   (assoc db :authenticated nil)))

;; You failed to logout. logout anyway
(kf/reg-event-db
 ::do-logout-failure
 (fn [db _]
   (assoc db :authenticated nil)))

(kf/reg-event-fx
 ::do-authenticate
 (fn [{:keys [db]} [data]]
   {:db (assoc db ::loading true)
    :http-xhrio
    {:method          :post
     :uri             (kf/path-for [:api-authenticate])
     :params          data
     :timeout         8000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-authenticate-success]
     :on-failure      [::do-authenticate-failure]}}))

(kf/reg-event-fx
 ::do-logout
 (fn [_ _]
   {:http-xhrio
    {:uri             (kf/path-for [:api-logout])
     :method          :post
     :on-success      [::do-logout-success]
     :on-failure      [::do-logout-failure]
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})}}))
