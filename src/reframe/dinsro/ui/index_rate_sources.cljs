(ns dinsro.ui.index-rate-sources
  (:require
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]))

(defn index-line
  [store item]
  (let [name (::m.rate-sources/name item)
        url (::m.rate-sources/url item)
        currency-id (get-in item [::m.rate-sources/currency :db/id])]
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
