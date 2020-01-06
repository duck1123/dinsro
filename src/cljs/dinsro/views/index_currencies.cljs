(ns dinsro.views.index-currencies
  (:require
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.create-currency :as c.f.create-currency]
   [dinsro.components.index-currencies :as c.index-currencies]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-currency :as e.f.create-currency]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]))

(defn init-page
  [_ _]
  {:dispatch [::e.currencies/do-fetch-index]
   :document/title "Index Currencies"})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-currencies-page)
  :start  [::init-page]})

(defn loading-buttons
  []
  [:div.box
   [c.buttons/fetch-currencies]])

(defn page
  [_]
  (let [currencies @(rf/subscribe [::e.currencies/items])]
    [:section.section>div.container>div.content
     (c.debug/hide [loading-buttons])
     [:div.box
      [:h1
       (tr [:index-currencies "Index Currencies"])
       [c/show-form-button ::e.f.create-currency/shown?]]
      [c.f.create-currency/form]
      [:hr]
      (when currencies
        [c.index-currencies/index-currencies currencies])]]))
