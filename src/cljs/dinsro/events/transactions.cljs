(ns dinsro.events.transactions
  (:require [ajax.core :as ajax]
            [cljc.java-time.instant :as instant]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.spec.transactions :as s.transactions]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(s/def ::items                   (s/coll-of ::s.transactions/item))
(rf/reg-sub ::items                  (fn [db _] (get db ::items                  [])))
(rf/reg-sub ::do-fetch-index-loading (fn [db _] (get db ::do-fetch-index-loading false)))

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [db [{:keys [items]}]]
  (timbre/info "fetch records success" items)
  (assoc db ::items items))

(defn do-fetch-index-failed
  [_ _]
  (timbre/info "fetch records failed"))

(defn do-fetch-index
  [{:keys [db]} [data]]
  {:db (assoc db ::do-fetch-index-loading true)
   :http-xhrio
   {:method          :get
    :uri             (kf/path-for [:api-index-transactions])
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-fetch-index-success]
    :on-failure      [::do-fetch-index-failed]}})

(kf/reg-event-db ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)
