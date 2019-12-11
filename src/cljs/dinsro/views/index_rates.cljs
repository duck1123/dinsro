(ns dinsro.views.index-rates
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-rate :as c.f.create-rate :refer [create-rate-form]]
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

(defn load-buttons
  []
  [:div.box
   [c.buttons/fetch-rates]
   [c.buttons/fetch-currencies]
   [c.buttons/toggle-debug]])

(defn show-form-button
  []
  (when-not @(rf/subscribe [::c.f.create-rate/shown?])
    [:a.is-pulled-right {:on-click #(rf/dispatch [::c.f.create-rate/set-shown? true])}
     "Show Form"]))

(defn page
  []
  (let [items @(rf/subscribe [::e.rates/items])]
    [:section.section>div.container>div.content
     #_[load-buttons]
     [:div.box
      [:h1
       (tr [:rates "Rates"])
       [show-form-button]]
      [create-rate-form]
      [:hr]
      [rate-chart items]
      [index-rates items]]]))
