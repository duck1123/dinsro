(ns dinsro.components.index-rates
  (:require [clojure.spec.alpha :as s]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(def rate-line-strings
  {:delete "Delete"
   :id "Id: "
   :no-rates "No Rates"
   :value "Value: "})

(def l rate-line-strings)

(defn-spec rate-line vector?
  [item ::s.rates/item]
  (let [strings rate-line-strings
        id (:db/id item)
        value (::s.rates/rate item)
        currency-id (get-in item [::s.rates/currency :db/id])
        currency @(rf/subscribe [::e.currencies/item currency-id])]
    [:tr
     [:td value]
     [:td (str (::s.rates/date item))]
     [:td (if currency
            [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])}
             (::s.currencies/name currency)]
            [:a {:on-click #(rf/dispatch [::e.currencies/do-fetch-record currency-id])}
             "Not Loaded"])]
     [:td
      [:a.button.is-danger
       {:on-click #(rf/dispatch [::e.rates/do-delete-record item])}
       (:delete strings)]]]))

(defn-spec index-rates vector?
  [items (s/coll-of ::s.rates/item)]
  [:<>
   #_[:pre (str items)]
   (if-not (seq items)
     [:p (l :no-rates)]
     [:table.table
      [:thead>tr
       [:th "Value"]
       [:th "Date"]
       [:th "Currency"]
       [:th "Actions"]]
      (->> (for [item items] ^{:key (:db/id item)} [rate-line item])
           (into [:tbody]))])])
