(ns dinsro.components.index-users
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items (fn [db _] (get db ::items [])))

(rf/reg-event-db
 ::init-component
 (fn-traced
  [db _]
  (assoc db ::users [])))

(defn index-users
  []
  (let [users @(rf/subscribe [::items])]
    [:div
     [:p (count users)]
     [:p (str users)]]))

(kf/reg-controller
 ::page
 {:id ::page
  :params (constantly nil)
  :start [::init-component]})

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Users Page"]
   [index-users]])
