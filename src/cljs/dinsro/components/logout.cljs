(ns dinsro.components.logout
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn page
  [])

(kf/reg-event-db
 ::do-logout-success
 (fn [db _]
   (timbre/info "logout success")
   (assoc db :authenticated nil)))

(kf/reg-event-db
 ::do-logout-failure
 (fn [db _]
   ;; FIXME: show failure
   (timbre/info "logout failure")
   (assoc db :authenticated nil)))

(kf/reg-event-fx
 ::do-logout
 (fn [_ _]
   {:http-xhrio
    {:uri             "/api/v1/logout"
     :method          :post
     :on-success      [::do-logout-success]
     :on-failure      [::do-logout-failure]
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})}}))
