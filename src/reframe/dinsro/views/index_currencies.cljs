(ns dinsro.views.index-currencies
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-currency :as e.f.create-currency]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.filters :as u.filters]
   [dinsro.ui.forms.create-currency :as u.f.create-currency]
   [dinsro.ui.index-currencies :as u.index-currencies]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:dispatch [::e.currencies/do-fetch-index]
   :document/title "Index Currencies"})

(defn loading-buttons
  [store]
  [:div.box
   [u.buttons/fetch-currencies store]])

(defn page
  [store _match]
  (let [currencies @(st/subscribe store [::e.currencies/items])]
    [:section.section>div.container>div.content
     (u.debug/hide store [loading-buttons store])
     [:div.box
      [:h1
       (tr [:index-currencies "Index Currencies"])
       [u.buttons/show-form-button store ::e.f.create-currency/shown?]]
      [u.f.create-currency/form store]
      [:hr]
      (when currencies
        [u.index-currencies/index-currencies store currencies])]]))

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
   {:params (u.filters/filter-page :index-currencies-page)
    :start  [::init-page]})

  store)
