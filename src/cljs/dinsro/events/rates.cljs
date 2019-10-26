(ns dinsro.events.rates
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(def rate {:id 1 :value 12158})

(rf/reg-sub ::items (fn [db _] (get db ::items [rate (update (update rate :value inc) :id inc)])))
(s/def ::items (s/* ::ds/rate))

(rf/reg-sub
 ::item
 :<- [::items]
 (fn [items [_ id]]
   (first (filter #(= (:id %) id) items))))

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
