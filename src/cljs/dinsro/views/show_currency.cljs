(ns dinsro.views.show-currency
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.currency-accounts :as c.currency-accounts]
            [dinsro.components.currency-rates :as c.currency-rates]
            [dinsro.components.currency-rate-sources :as c.currency-rate-sources]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.show-currency :as c.show-currency]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rate-sources :as e.rate-sources]
            [dinsro.events.rates :as e.rates]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.categories :as s.categories]
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
  (c.debug/hide
   [:div.box
    [c.buttons/fetch-rates]
    [c.buttons/fetch-accounts]
    [c.buttons/fetch-currencies]
    [c.buttons/fetch-rate-sources]
    [c.buttons/fetch-currency id]]))

(s/def :show-currency-view/id          string?)
(s/def :show-currency-view/path-params (s/keys :req-un [:show-currency-view/id]))
(s/def ::view-map                      (s/keys :req-un [:show-currency-view/path-params]))

(defn page-loaded
  [currency]
  (let [currency-id (:db/id currency)]
    [:<>
     [:div.box [c.show-currency/show-currency currency]]
     (when-let [rates @(rf/subscribe [::e.rates/items-by-currency currency])]
       [c.currency-rates/section currency-id rates])
     (when-let [accounts (some->> @(rf/subscribe [::e.accounts/items-by-currency currency])
                                  (sort-by ::s.accounts/date))]
       [c.currency-accounts/section accounts])
     (when-let [rate-sources @(rf/subscribe [::e.rate-sources/items
                                             ;; -by-currency currency
                                             ])]
       [c.currency-rate-sources/section currency-id rate-sources])]))

(s/fdef page-loaded
  :args (s/cat :currency ::s.categories/item)
  :ret vector?)

(defn page
  [{{:keys [id]} :path-params}]
  (let [currency-id (int id)
        currency @(rf/subscribe [::e.currencies/item currency-id])
        state @(rf/subscribe [::e.currencies/do-fetch-record-state])]
    [:section.section>div.container>div.content
     [loading-buttons id]
     (condp = state
       :loaded [page-loaded currency]
       :loading [:p "Loading"]
       :failed [:p "Failed"]
       [:p "Unknown State"])]))

(s/fdef page
  :args (s/cat :match ::view-map)
  :ret vector?)
