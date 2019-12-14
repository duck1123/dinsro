(ns dinsro.components.index-currencies
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.links :as c.links]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.translations :refer [tr]]
            [dinsro.views.show-currency :as show-currency]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn-spec index-currency-line vector?
  [currency ::s.currencies/item]
  (let [{:keys [db/id]} currency]
    [:tr
     [:td [c.links/currency-link id]]
     [:td [c.buttons/delete-currency currency]]]))

(defn-spec index-currencies vector?
  [currencies (s/coll-of ::s.currencies/item)]
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
