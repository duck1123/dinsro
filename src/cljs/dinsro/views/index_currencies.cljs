(ns dinsro.views.index-currencies
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.buttons :as button]
            [dinsro.components.forms.create-currency :refer [create-currency]]
            [dinsro.components.index-currencies :refer [index-currencies]]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(kf/reg-event-fx
 ::init-page
 (fn-traced [_ _]
   {:dispatch [::e.currencies/do-fetch-index]}))

(kf/reg-controller
 ::page-controller
 {:params #(when (= (get-in % [:data :name]) :index-currencies-page) true)
  :start  [::init-page]})

(defn page
  []
  (let [currencies @(rf/subscribe [::e.currencies/items])]
    [:section.section>div.container>div.content
     [:h1 "Index Currencies"]
     [button/fetch-currencies]
     [create-currency]
     [index-currencies currencies]]))
