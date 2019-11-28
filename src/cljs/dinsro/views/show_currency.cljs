(ns dinsro.views.show-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components.show-currency :refer [show-currency]]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  (let [id 45]
    {:dispatch [::e.currencies/do-fetch-record id]}))

(defn filter-page
  [page]
  #(when (= (get-in % [:data :name]) :show-currency-page) true))

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (filter-page :show-currency-page)
  :start  [::init-page]})

(s/def :show-currency-view/id          pos-int?)
(s/def :show-currency-view/path-params (s/keys :req-un [:show-currency-view/id]))
(s/def ::view-map                      (s/keys :req-un [:show-currency-view/path-params]))

(defn-spec page vector?
  [{{:keys [id]} :path-params} ::view-map]
  (let [currency-id (int id)
        currency @(rf/subscribe [::e.currencies/item currency-id])
        rates [#_{:db/id 1}] #_@(rf/subscribe [::e.rates/items-by-currency currency])]
    [:section.section>div.container>div.content
     [:button.button "Load Currency"]
     [show-currency currency rates]]))
