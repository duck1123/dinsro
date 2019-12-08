(ns dinsro.views.show-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.add-currency-rate :refer [add-currency-rate-form]]
            [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.components.show-currency :refer [show-currency]]
            [dinsro.events.currencies :as e.currencies]
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
    {:dispatch [::e.currencies/do-fetch-record id]}))

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-currency-page)
  :start  [::init-page]})

;; Fixme: string
(s/def :show-currency-view/id          string?)
(s/def :show-currency-view/path-params (s/keys :req-un [:show-currency-view/id]))
(s/def ::view-map                      (s/keys :req-un [:show-currency-view/path-params]))

(defn-spec page vector?
  [{{:keys [id]} :path-params} ::view-map]
  (let [currency-id (int id)
        currency @(rf/subscribe [::e.currencies/item currency-id])
        rates @(rf/subscribe [::e.rates/items-by-currency currency])]
    [:section.section>div.container>div.content
     [:div.box
      [c.buttons/fetch-rates]
      [c.buttons/fetch-currencies]
      [c.buttons/fetch-currency id]]
     (let [state @(rf/subscribe [::e.currencies/do-fetch-record-state])]
       [:div
        (condp = state
          :loaded [show-currency currency rates]
          :loading [:p "Loading"]
          :failed [:p "Failed"]
          [:p "Unknown State"])
        [:div
         [:hr]
         [add-currency-rate-form currency-id]
         [index-rates rates]]])]))
