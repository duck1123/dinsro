(ns dinsro.views.show-currency
  (:require
   [clojure.spec.alpha :as s]
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
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.spec.views.show-currency :as s.v.show-currency]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ [{:keys [id]}]]
  {:dispatch-n [[::e.currencies/do-fetch-record id]
                [::e.rates/do-fetch-rate-feed-by-currency (int id)]
                [::e.users/do-fetch-index]
                [::e.accounts/do-fetch-index]]
   :document/title "Show Currency"})

(s/fdef init-page
  :args (s/cat :cofx ::s.v.show-currency/init-page-cofx
               :event ::s.v.show-currency/init-page-event)
  :ret ::s.v.show-currency/init-page-response)

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-currency-page)
  :start  [::init-page]})

(defn loading-buttons
  [id]
  [:<>
   (c.debug/hide
    [:div.box
     [c.buttons/fetch-rates]
     [c.buttons/fetch-accounts]
     [c.buttons/fetch-currencies]
     [c.buttons/fetch-rate-sources]
     [c.buttons/fetch-currency id]])])

(s/fdef loading-buttons
  :args (s/cat :id :db/id)
  :ret vector?)

(defn page-loaded
  [currency]
  (let [currency-id (:db/id (timbre/spy :info currency))]
    [:<>
     [:div.box [c.show-currency/show-currency currency]]
     (when-let [rates @(rf/subscribe [::e.rates/rate-feed (:db/id currency)])]
       [c.currency-rates/section currency-id rates])
     (when-let [accounts (some->> @(rf/subscribe [::e.accounts/items-by-currency currency])
                                  (sort-by ::s.accounts/date))]
       [c.currency-accounts/section accounts])
     (when-let [rate-sources @(rf/subscribe [::e.rate-sources/items
                                             ;; -by-currency currency
                                             ])]
       [c.currency-rate-sources/section currency-id rate-sources])]))

(s/fdef page-loaded
  :args (s/cat :currency ::s.currencies/item)
  :ret vector?)

(defn page
  [{{:keys [id]} :path-params}]
  (let [currency-id (int id)
        currency @(rf/subscribe [::e.currencies/item currency-id])
        state @(rf/subscribe [::e.currencies/do-fetch-record-state])]
    [:section.section>div.container>div.content
     [loading-buttons currency-id]
     (condp = state
       :loaded [page-loaded currency]
       :loading [:p "Loading"]
       :failed [:p "Failed"]
       [:p "Unknown State"])]))

(s/fdef page
  :args (s/cat :match ::s.v.show-currency/view-map)
  :ret vector?)
