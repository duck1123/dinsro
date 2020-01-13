(ns dinsro.components.index-currencies
  (:require
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.links :as c.links]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn index-currency-line
  [currency]
  (let [{:keys [db/id]} currency]
    [:tr
     [:td [c.links/currency-link id]]
     (c.debug/hide [:td [c.buttons/delete-currency currency]])]))

(defn index-currencies
  [currencies]
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
