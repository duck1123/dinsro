(ns dinsro.views.index-rates
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-rate-source :as c.f.create-rate-source]
            [dinsro.components.index-rate-sources :as c.index-rate-sources]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.rate-sources :as e.rate-sources]
            [dinsro.spec.events.forms.create-rate-source :as s.e.f.create-rate-source]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:db (assoc db ::e.rate-sources/items [])
   :document/title "Index Rates Sources"
   :dispatch-n [
                #_[::e.currencies/do-fetch-index]
                [::e.rate-sources/do-fetch-index]
                ]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-rates-page)
  :start [::init-page]})

(defn-spec load-buttons vector?
  []
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-rate-sources]
     #_[c.buttons/fetch-currencies]]))

(defn-spec page vector?
  []
  (let [items @(rf/subscribe [::e.rates/items])]
    [:section.section>div.container>div.content
     [load-buttons]
     [:div.box
      [:h1
       (tr [:rates "Rate Sources"])
       [c/show-form-button ::s.e.f.create-rate-source/shown? ::s.e.f.create-rate-source/set-shown?]]
      [c.f.create-rate-source/form]
      [:hr]
      #_[rate-chart items]
      [c.index-rate-sources/section items]]]))
