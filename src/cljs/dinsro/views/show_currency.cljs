(ns dinsro.views.show-currency
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.add-currency-rate :as c.f.add-currency-rate]
            [dinsro.components.index-accounts :as c.index-accounts]
            [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.components.rate-chart :as c.rate-chart]
            [dinsro.components.show-currency :refer [show-currency]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.rates :as e.rates]
            [dinsro.events.users :as e.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(defn init-page
  [_ [{:keys [id]}]]
  {:dispatch-n [[::e.currencies/do-fetch-record id]
                [::e.rates/do-fetch-index]
                [::e.users/do-fetch-index]
                [::e.accounts/do-fetch-index]]
   :document/title "Show Currency"})

(s/fdef init-page
  :args (s/cat :cofx ::init-page-cofx
               :event ::init-page-event)
  :ret ::init-page-response)

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
     [c.buttons/fetch-accounts]
     [c.buttons/fetch-currencies]
     [c.buttons/fetch-currency id]]))

(s/def :show-currency-view/id          string?)
(s/def :show-currency-view/path-params (s/keys :req-un [:show-currency-view/id]))
(s/def ::view-map                      (s/keys :req-un [:show-currency-view/path-params]))

(defn rates-section
  [currency-id rates]
  [:div.box
   [:h2 "Rates"]
   [c.f.add-currency-rate/form currency-id]
   [:hr]
   [c.rate-chart/rate-chart rates]
   [index-rates rates]])

(defn accounts-section
  [accounts]
  [:div.box
   [:h2 "Accounts"]
   [c.index-accounts/index-accounts accounts]])

(defn page
  [{{:keys [id]} :path-params}]
  (let [currency-id (int id)
        currency @(rf/subscribe [::e.currencies/item currency-id])
        rates @(rf/subscribe [::e.rates/items-by-currency currency])
        state @(rf/subscribe [::e.currencies/do-fetch-record-state])
        accounts @(rf/subscribe [::e.accounts/items-by-currency currency])]
    [:section.section>div.container>div.content
     [loading-buttons id]
     [:div.box
      (condp = state
        :loaded [show-currency currency]
        :loading [:p "Loading"]
        :failed [:p "Failed"]
        [:p "Unknown State"])]
     [accounts-section accounts]
     [rates-section currency-id rates]]))

(s/fdef page
  :args (s/cat :match ::view-map)
  :ret vector?)
