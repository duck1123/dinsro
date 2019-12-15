(ns dinsro.components.index-rates
  (:require [clojure.spec.alpha :as s]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn delete-button
  [item]
  [:a.button.is-danger
   {:on-click #(rf/dispatch [::e.rates/do-delete-record item])}
   (tr [:delete])])

(defn-spec rate-line vector?
  [item ::s.rates/item]
  (let [id (:db/id item)
        value (::s.rates/rate item)
        currency-id (get-in item [::s.rates/currency :db/id])
        currency @(rf/subscribe [::e.currencies/item currency-id])]
    [:tr
     [:td (.toISOString (::s.rates/date item))]
     [:td value]
     [:td (if currency
            [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])}
             (::s.currencies/name currency)]
            [:a {:on-click #(rf/dispatch [::e.currencies/do-fetch-record currency-id])}
             (tr [:not-loaded])])]
     #_[:td [delete-button item]]]))

(defn-spec index-rates vector?
  [items (s/coll-of ::s.rates/item)]
  [:<>
   #_[:pre (str items)]
   (if-not (seq items)
     [:p (tr [:no-rates])]
     [:table.table
      [:thead>tr
       [:th (tr [:date])]
       [:th (tr [:value])]
       [:th (tr [:currency])]
       #_[:th (tr [:actions])]]
      (->> (for [item items] ^{:key (:db/id item)} [rate-line item])
           (into [:tbody]))])])
