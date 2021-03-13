(ns dinsro.ui.index-rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]))

(defn rate-line
  [store item]
  (let [value (::m.rates/rate item)
        currency-id (get-in item [::m.rates/currency :db/id])]
    [:tr
     [:td (str (::m.rates/date item))]
     [:td value]
     [:td [u.links/currency-link store currency-id]]
     (u.debug/hide store [:td [u.buttons/delete-rate store item]])]))

(defn section
  [store items]
  (if-not (seq items)
    [:p (tr [:no-rates])]
    [:table.table
     [:thead>tr
      [:th (tr [:date])]
      [:th (tr [:value])]
      [:th (tr [:currency])]
      (u.debug/hide store [:th (tr [:actions])])]
     (into
      [:tbody]
      (for [item items]
        ^{:key (:db/id item)} [rate-line store item]))]))

(s/fdef section
  :args (s/cat :items (s/coll-of ::m.rates/item))
  :ret vector?)
