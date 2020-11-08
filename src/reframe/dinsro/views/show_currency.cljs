(ns dinsro.views.show-currency
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.rates :as e.rates]
   [dinsro.events.users :as e.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs.views.show-currency :as s.v.show-currency]
   [dinsro.store :as st]
   [dinsro.ui :as u]
   [dinsro.ui.currency-accounts :as u.currency-accounts]
   [dinsro.ui.currency-rates :as u.currency-rates]
   [dinsro.ui.currency-rate-sources :as u.currency-rate-sources]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.show-currency :as u.show-currency]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ [{:keys [id]}]]
  {:dispatch-n [[::e.currencies/do-fetch-record id]
                [::e.rates/do-fetch-rate-feed-by-currency (int id)]
                [::e.rate-sources/do-fetch-index]
                [::e.users/do-fetch-index]
                [::e.accounts/do-fetch-index]]
   :document/title "Show Currency"})

(s/fdef init-page
  :args (s/cat :cofx ::s.v.show-currency/init-page-cofx
               :event ::s.v.show-currency/init-page-event)
  :ret ::s.v.show-currency/init-page-response)

(defn loading-buttons
  [store id]
  [:<>
   (u.debug/hide store
    [:div.box
     [u.buttons/fetch-rates store]
     [u.buttons/fetch-accounts store]
     [u.buttons/fetch-currencies store]
     [u.buttons/fetch-rate-sources store]
     [u.buttons/fetch-currency store id]])])

(s/fdef loading-buttons
  :args (s/cat :id :db/id)
  :ret vector?)

(defn page-loaded
  [store currency]
  (let [currency-id (:db/id currency)]
    [:<>
     [:div.box [u.show-currency/show-currency store currency]]
     (when-let [rates @(st/subscribe store [::e.rates/rate-feed currency-id])]
       [u.currency-rates/section store rates])
     (when-let [accounts (some->> @(st/subscribe store [::e.accounts/items-by-currency currency])
                                  (sort-by ::m.accounts/date))]
       [u.currency-accounts/section store accounts])
     (when-let [rate-sources @(st/subscribe store [::e.rate-sources/items
                                             ;; -by-currency currency
                                             ])]
       [u.currency-rate-sources/section store currency-id rate-sources])]))

(s/fdef page-loaded
  :args (s/cat :currency ::m.currencies/item)
  :ret vector?)

(defn page
  [store {{:keys [id]} :path-params}]
  (let [currency-id (int id)
        currency @(st/subscribe store [::e.currencies/item currency-id])
        state @(st/subscribe store [::e.currencies/do-fetch-record-state])]
    [:section.section>div.container>div.content
     [loading-buttons store currency-id]
     (condp = state
       :loaded [page-loaded store currency]
       :loading [:p "Loading"]
       :failed [:p "Failed"]
       [:p "Unknown State"])]))

(s/fdef page
  :args (s/cat :match ::s.v.show-currency/view-map)
  :ret vector?)

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match ::s.v.show-currency/view-map ;; #(instance? rc/Match %)
               )
  :ret vector?)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page-controller
   {:params (u/filter-param-page :show-currency-page)
    :start  [::init-page]})

  store)
