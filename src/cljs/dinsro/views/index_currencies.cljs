(ns dinsro.views.index-currencies
  (:require [dinsro.components.index-currencies :refer [index-currencies]]
            [dinsro.events.currencies :as e.currencies]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(kf/reg-event-fx
 ::init-page
 (fn-traced [_ _]
   {:dispatch [::e.currencies/do-fetch-index]}))

(kf/reg-controller
 ::page-controller
 {:params #(when (= (get-in % [:data :name]) ::page) true)
  :start  [::init-page]})

(defn page
  []
  (let [currencies @(rf/subscribe [::e.currencies/items])]
    [:section.section>div.container>div.content
     [:h1 "Index Currencies"]
     [:a.button {:on-click #(rf/dispatch [::init-page])} "Load"]
     [index-currencies currencies]]))
