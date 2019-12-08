(ns dinsro.views.index-currencies
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-currency :refer [create-currency]]
            [dinsro.components.index-currencies :refer [index-currencies]]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:dispatch [::e.currencies/do-fetch-index]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params #(when (= (get-in % [:data :name]) :index-currencies-page) true)
  :start  [::init-page]})

(defn page
  []
  (let [currencies @(rf/subscribe [::e.currencies/items])]
    [:section.section>div.container>div.content
     [:h1 (tr [:index-currencies "Index Currencies"])]
     [:div.box
      [c.buttons/fetch-currencies]
      [c.buttons/toggle-debug]]
     [:div.box
      [create-currency]
      [:hr]
      [index-currencies currencies]]]))
