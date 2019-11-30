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
   :value "Value: "})

(defn-spec rate-line vector?
  [item ::s.rates/item]
  (let [strings rate-line-strings
        id (:db/id item)
        value (::s.rates/rate item)
        currency-id (get-in item [::s.rates/currency :db/id])
        currency @(rf/subscribe [::e.currencies/item currency-id])]
    [:div.box #_{:style {:border "1px black solid"}}
     #_[:pre (str item)]
     #_[:p (:id strings) id]
     [:div.column
      [:p (:value strings) value]
      [:p "Currency: "
       [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])}
        (::s.currencies/name currency)]]]
     [:div.column
      [:a.button.is-danger
       {:on-click #(rf/dispatch [::e.rates/do-delete-record item])}
       (:delete strings)]]]))

(defn-spec index-rates vector?
  [items (s/coll-of ::s.rates/item)]
  [:section
   #_[:pre (str items)]
   (let [strings {:no-rates "No Rates"}]
     (if-not (seq items)
       [:p (:no-rates strings)]
       (->> (for [item items] ^{:key (:db/id item)} [rate-line item])
            (into [:div]))))])
