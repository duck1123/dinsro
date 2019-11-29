(ns dinsro.views.show-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
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

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :show-currency-page)
  :start  [::init-page]})

(s/def :show-currency-view/id          pos-int?)
(s/def :show-currency-view/path-params (s/keys :req-un [:show-currency-view/id]))
(s/def ::view-map                      (s/keys :req-un [:show-currency-view/path-params]))

(def l
  {:load-currency "Load Currency"
   :not-loaded "Currency notation loaded"})

(defn-spec page vector?
  [{{:keys [id]} :path-params} ::view-map]
  (let [currency-id (int id)
        currency @(rf/subscribe [::e.currencies/item currency-id])
        rates @(rf/subscribe [::e.rates/items-by-currency currency])]
    [:section.section>div.container>div.content
     [:p @(rf/subscribe [::e.currencies/do-fetch-record-state])]
     [:button.button {:on-click #(rf/dispatch [::e.currencies/do-fetch-record id])}
      (l :load-currency)]
     (if (nil? currency)
       [:p (l :not-loaded)]
       [show-currency currency rates])]))
