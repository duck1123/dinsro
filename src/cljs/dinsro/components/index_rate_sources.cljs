(ns dinsro.components.index-rate-sources
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.events.rate-sources :as e.rate-sources]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn-spec index-line vector?
  [item ::s.rate-sources/item]
  (let [id (:db/id item)
        value (::s.rate-sources/name item)
        ;; currency-id (get-in item [::s.rates/currency :db/id])
        ]
    [:tr
     ;; [:td (str (::s.rates/date item))]
     [:td value]
     ;; [:td [c.links/currency-link currency-id]]
     (c.debug/hide [:td [c.buttons/delete-rate item]])]))

(defn-spec section vector?
  [items (s/coll-of ::s.rate-sources/item)]
  [:<>
   [c.debug/debug-box items]
   (if-not (seq items)
     [:p (tr [:no-rate-sources])]
     [:table.table
      [:thead>tr
       ;; [:th (tr [:date])]
       [:th (tr [:value])]
       ;; [:th (tr [:currency])]
       (c.debug/hide [:th (tr [:actions])])]
      (->> (for [item items] ^{:key (:db/id item)} [index-line item])
           (into [:tbody]))])])
