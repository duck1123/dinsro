(ns dinsro.components.index-currencies
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.links :as c.links]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.translations :refer [tr]]
            [dinsro.views.show-currency :as show-currency]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn index-currency-line
  [currency]
  (let [{:keys [db/id]} currency]
    [:tr
     [:td [c.links/currency-link id]]
     [:td [c.buttons/delete-currency]]]))

(defn index-currencies
  [currencies]
  [:<>
   [c/debug-box currencies]
   (if-not (seq currencies)
     [:div (tr [:no-currencies])]
     [:table
      [:thead>tr
       [:th (tr [:name-label])]
       [:th "Buttons"]]
      (into
       [:tbody]
       (for [{:keys [db/id] :as currency} currencies]
         ^{:key id} [index-currency-line currency]))])])
