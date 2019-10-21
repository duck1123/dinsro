(ns dinsro.events.rates
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.login :as login]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items (fn [db _] (get db ::items [])))

(rf/reg-event-fx
 ::do-submit
 (fn-traced
   [{:keys [db]} [_ data]]
   {:db (assoc db ::do-submit-loading true)
    :http-xhrio
    {:method :post
     :uri (kf/path-for [:api-index-users])
     :params data
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success [::do-submit-succeeded]
     :on-failure [::do-submit-failed]}}))
