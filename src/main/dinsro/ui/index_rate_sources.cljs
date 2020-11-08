(ns dinsro.ui.index-rate-sources
  (:require
   [dinsro.specs.rate-sources :as s.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]))

(defn index-line
  [store item]
  (let [name (::s.rate-sources/name item)
        url (::s.rate-sources/url item)
        currency-id (get-in item [::s.rate-sources/currency :db/id])]
    [:tr
     [:td name]
     [:td url]
     [:td [u.links/currency-link store currency-id]]
     (u.debug/hide store [:td [u.buttons/delete-rate-source store item]])]))

(defn section
  [store items]
  (if-not (seq items)
    [:p (tr [:no-rate-sources])]
    [:table.table
     [:thead>tr
      [:th (tr [:name])]
      [:th (tr [:url])]
      [:th (tr [:currency])]
      (u.debug/hide store [:th (tr [:actions])])]
     (into
      [:tbody]
      (for [item items]
        ^{:key (:db/id item)} [index-line store item]))]))
