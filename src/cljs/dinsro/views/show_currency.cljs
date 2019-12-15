(ns dinsro.views.show-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.add-currency-rate :refer [add-currency-rate-form]]
            [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.components.show-currency :refer [show-currency]]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.rates :as e.rates]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(defn-spec init-page ::init-page-response
  [cofx ::init-page-cofx
   event ::init-page-event]
  (let [[{:keys [id]}] event]
    {:dispatch [::e.currencies/do-fetch-record id]
     :document/title "Show Currency"}))

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-currency-page)
  :start  [::init-page]})

(defn loading-buttons
  [id]
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-rates]
     [c.buttons/fetch-currencies]
     [c.buttons/fetch-currency id]]))

(s/def :show-currency-view/id          string?)
(s/def :show-currency-view/path-params (s/keys :req-un [:show-currency-view/id]))
(s/def ::view-map                      (s/keys :req-un [:show-currency-view/path-params]))

(defn-spec page vector?
  [{{:keys [id]} :path-params} ::view-map]
  (let [currency-id (int id)
        currency @(rf/subscribe [::e.currencies/item currency-id])
        rates @(rf/subscribe [::e.rates/items-by-currency currency])
        state @(rf/subscribe [::e.currencies/do-fetch-record-state])]
    [:section.section>div.container>div.content
     [loading-buttons id]
     [:div.box
      (condp = state
        :loaded [show-currency currency]
        :loading [:p "Loading"]
        :failed [:p "Failed"]
        [:p "Unknown State"])]
     [:div.box
      [:h2 "Rates"]
      [add-currency-rate-form currency-id]
      [:hr]
      [index-rates rates]]]))
