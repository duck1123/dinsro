(ns dinsro.components.index-currencies
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec index-currency-line vector?
  [currency ::s.currencies/item]
  (let [{:keys [db/id]} currency]
    [:tr
     [:td [c.links/currency-link id]]
     (c.debug/hide [:td [c.buttons/delete-currency currency]])]))

(defn-spec index-currencies vector?
  [currencies (s/coll-of ::s.currencies/item)]
  [:<>
   [c.debug/debug-box currencies]
   (if-not (seq currencies)
     [:div (tr [:no-currencies])]
     [:table
      [:thead>tr
       [:th (tr [:name-label])]
       (c.debug/hide [:th "Buttons"])]
      (into
       [:tbody]
       (for [{:keys [db/id] :as currency} currencies]
         ^{:key id} [index-currency-line currency]))])])
