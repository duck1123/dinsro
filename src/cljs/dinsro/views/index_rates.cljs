(ns dinsro.views.index-rates
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.buttons :as button]
            [dinsro.components.forms.create-rate :refer [create-rate-form]]
            [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.components.rate-chart :refer [rate-chart]]
            [dinsro.events.rates :as e.rates]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:db (-> db
           (assoc :failed false)
           (assoc ::e.rates/items [])
           (assoc ::loading false))
   :dispatch [::e.rates/do-fetch-index]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params #(when (= (get-in % [:data :name]) :index-rates-page) true)
  :start [::init-page]})

(defn page
  []
  (let [items @(rf/subscribe [::e.rates/items])]
    [:section.section>div.container>div.content
     [:h1 (tr [:rates "Rates"])]
     #_[rate-chart items]
     [button/fetch-rates]
     [create-rate-form]
     [index-rates items]]))
