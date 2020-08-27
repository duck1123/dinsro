(ns dinsro.components.index-rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.links :as c.links]
   [dinsro.spec.rates :as s.rates]
   [dinsro.translations :refer [tr]]))

(defn rate-line
  [store item]
  (let [value (::s.rates/rate item)
        currency-id (get-in item [::s.rates/currency :db/id])]
    [:tr
     [:td (str (::s.rates/date item))]
     [:td value]
     [:td [c.links/currency-link store currency-id]]
     (c.debug/hide store [:td [c.buttons/delete-rate store item]])]))

(defn section
  [store items]
  [:<>
   [c.debug/debug-box store items]
   (if-not (seq items)
     [:p (tr [:no-rates])]
     [:table.table
      [:thead>tr
       [:th (tr [:date])]
       [:th (tr [:value])]
       [:th (tr [:currency])]
       (c.debug/hide store [:th (tr [:actions])])]
      (into
       [:tbody]
       (for [item items]
         ^{:key (:db/id item)} [rate-line store item]))])])

(s/fdef section
  :args (s/cat :items (s/coll-of ::s.rates/item))
  :ret vector?)
