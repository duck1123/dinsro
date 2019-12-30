(ns dinsro.views.index-rates
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-rate :as c.f.create-rate]
            [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.components.rate-chart :refer [rate-chart]]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.rates :as e.rates]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:db (assoc db ::e.rates/items [])
   :document/title "Index Rates"
   :dispatch-n [[::e.currencies/do-fetch-index]
                [::e.rates/do-fetch-index]]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-rates-page)
  :start [::init-page]})

(defn load-buttons
  []
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-rates]
     [c.buttons/fetch-currencies]]))

(defn page
  []
  (let [items @(rf/subscribe [::e.rates/items])]
    [:section.section>div.container>div.content
     [load-buttons]
     [:div.box
      [:h1
       (tr [:rates "Rates"])
       [c/show-form-button ::c.f.create-rate/shown? ::c.f.create-rate/set-shown?]]
      [c.f.create-rate/form]
      [:hr]
      [rate-chart items]
      [index-rates items]]]))
