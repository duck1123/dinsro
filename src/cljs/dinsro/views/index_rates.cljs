(ns dinsro.views.index-rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.create-rate :as c.f.create-rate]
   [dinsro.components.index-rates :as c.index-rates]
   [dinsro.components.rate-chart :as c.rate-chart]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.events.rates :as e.rates]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [reitit.core :as rc]))

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
  [:div.box
   [c.buttons/fetch-rates]
   [c.buttons/fetch-currencies]])

(defn page
  [store _match]
  (let [items @(st/subscribe store [::e.rates/items])]
    [:section.section>div.container>div.content
     (c.debug/hide [load-buttons])
     [:div.box
      [:h1
       (tr [:rates "Rates"])
       [c/show-form-button ::e.f.create-rate/shown?]]
      [c.f.create-rate/form]
      [:hr]
      [c.rate-chart/rate-chart items]
      [c.index-rates/section items]]]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)
