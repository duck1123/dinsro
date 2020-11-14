(ns dinsro.views.index-rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.events.rates :as e.rates]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.filters :as u.filters]
   [dinsro.ui.forms.create-rate :as u.f.create-rate]
   [dinsro.ui.index-rates :as u.index-rates]
   [dinsro.ui.rate-chart :as u.rate-chart]
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
   [u.buttons/fetch-rates store]
   [u.buttons/fetch-currencies store]])

(defn page
  [store _match]
  (let [items @(st/subscribe store [::e.rates/items])]
    [:section.section>div.container>div.content
     (u.debug/hide store [load-buttons store])
     [:div.box
      [:h1
       (tr [:rates "Rates"])
       [u.buttons/show-form-button store ::e.f.create-rate/shown?]]
      [u.f.create-rate/form store]
      [:hr]
      [u.rate-chart/rate-chart store items]
      [u.index-rates/section store items]]]))

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
   {:params (u.filters/filter-page :index-rates-page)
    :start [::init-page]})

  store)
