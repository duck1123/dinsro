(ns dinsro.components.index-rates
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
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

(defn-spec rate-line vector?
  [item ::s.rates/item]
  (let [id (:db/id item)
        value (::s.rates/rate item)
        currency-id (get-in item [::s.rates/currency :db/id])]
    [:tr
     [:td (.toISOString (::s.rates/date item))]
     [:td value]
     [:td [c.links/currency-link currency-id]]
     (c.debug/hide [:td [c.buttons/delete-rate item]])]))

(defn-spec index-rates vector?
  [items (s/coll-of ::s.rates/item)]
  [:<>
   [c.debug/debug-box items]
   (if-not (seq items)
     [:p (tr [:no-rates])]
     [:table.table
      [:thead>tr
       [:th (tr [:date])]
       [:th (tr [:value])]
       [:th (tr [:currency])]
       (c.debug/hide [:th (tr [:actions])])]
      (->> (for [item items] ^{:key (:db/id item)} [rate-line item])
           (into [:tbody]))])])
