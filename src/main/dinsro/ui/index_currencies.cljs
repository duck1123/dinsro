(ns dinsro.ui.index-currencies
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(defn index-currency-line
  [store currency]
  (let [{:keys [db/id]} currency]
    [:tr
     [:td [u.links/currency-link store id]]
     (u.debug/hide store [:td [u.buttons/delete-currency store currency]])]))

(defn index-currencies
  [store currencies]
  (if-not (seq currencies)
    [:div (tr [:no-currencies])]
    [:table
     [:thead>tr
      [:th (tr [:name-label])]
      (u.debug/hide store [:th "Buttons"])]
     (into
      [:tbody]
      (for [{:keys [db/id] :as currency} currencies]
        ^{:key id} [index-currency-line store currency]))]))
