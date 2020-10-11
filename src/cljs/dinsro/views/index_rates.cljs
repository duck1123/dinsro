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
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:db (assoc db ::e.rates/items [])
   :document/title "Index Rates"
   :dispatch-n [[::e.currencies/do-fetch-index]
                [::e.rates/do-fetch-index]]})

(defn load-buttons
  [store]
  [:div.box
   [c.buttons/fetch-rates store]
   [c.buttons/fetch-currencies store]])

(defn page
  [store _match]
  (let [items @(st/subscribe store [::e.rates/items])]
    [:section.section>div.container>div.content
     (c.debug/hide store [load-buttons store])
     [:div.box
      [:h1
       (tr [:rates "Rates"])
       [c/show-form-button store ::e.f.create-rate/shown?]]
      [c.f.create-rate/form store]
      [:hr]
      [c.rate-chart/rate-chart store items]
      [c.index-rates/section store items]]]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page-controller
   {:params (c/filter-page :index-rates-page)
    :start [::init-page]})

  store)
