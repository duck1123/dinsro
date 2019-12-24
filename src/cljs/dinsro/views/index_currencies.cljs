(ns dinsro.views.index-currencies
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-currency :as c.f.create-currency]
            [dinsro.components.index-currencies :as c.index-currencies]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.spec.events.forms.create-currency :as s.e.f.create-currency]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:dispatch [::e.currencies/do-fetch-index]
   :document/title "Index Currencies"})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-currencies-page)
  :start  [::init-page]})

(defn-spec loading-buttons vector?
  []
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-currencies]]))

(defn-spec page vector?
  [_ any?]
  (let [currencies @(rf/subscribe [::e.currencies/items])]
    [:section.section>div.container>div.content
     [loading-buttons]
     [:div.box
      [:h1
       (tr [:index-currencies "Index Currencies"])
       [c/show-form-button ::e.f.create-currency/shown? ::e.f.create-currency/set-shown?]]
      [c.f.create-currency/form]
      [:hr]
      (when currencies
        [c.index-currencies/index-currencies currencies])]]))
