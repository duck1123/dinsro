(ns dinsro.components.index-currencies
  (:require [dinsro.components.buttons :as c.buttons]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.translations :refer [tr]]
            [dinsro.views.show-currency :as show-currency]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn currency-link
  [currency-id]
  (if-let [currency @(rf/subscribe [::e.currencies/item currency-id])]
    (let [name (::s.currencies/name currency)]
      [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])} name])
    (tr [:no-currency] ["Load currency button"])))

(defn index-currency-line
  [currency]
  (let [{:keys [db/id]} currency]
    [:tr
     [:td [currency-link id]]
     [:td [c.buttons/delete-currency]]]))

(defn index-currencies
  [currencies]
  [:<>
   #_[:pre (str currencies)]
   (if-not (seq currencies)
     [:div (tr [:no-currencies]) ]
     [:table
      [:thead>tr
       [:th (tr [:name-label])]
       [:th "Buttons"]]
      (into
       [:tbody]
       (for [{:keys [db/id] :as currency} currencies]
         ^{:key id} [index-currency-line currency]))])])
