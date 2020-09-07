(ns dinsro.components.admin-index-rate-sources
  (:require
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.links :as c.links]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.spec.rate-sources :as s.rate-sources]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]))

(defn index-line
  [store item]
  (let [id (:db/id item)
        name (::s.rate-sources/name item)
        url (::s.rate-sources/url item)
        currency-id (get-in item [::s.rate-sources/currency :db/id])]
    [:tr
     [:td id]
     [:td name]
     [:td url]
     [:td [c.links/currency-link store currency-id]]
     [:td
      [:button.button
       {:on-click #(st/dispatch store [::e.rate-sources/do-run-source id])}
       "Run"]
      [c.buttons/delete-rate store item]]]))

(defn rate-sources-table
  [store items]
  (if-not (seq items)
    [:p (tr [:no-rate-sources])]
    [:table.table
     [:thead>tr
      [:th (tr [:id])]
      [:th (tr [:name])]
      [:th (tr [:url])]
      [:th (tr [:currency])]
      [:th (tr [:actions])]]
     (into
      [:tbody]
      (for [item items] ^{:key (:db/id item)} [index-line store item]))]))

(defn section-inner
  [store items]
  [:div.box
   [:h2.title.is-2 (tr [:rate-sources])]
   (rate-sources-table store @items)])

(defn section
  [store]
  (let [items (st/subscribe store [::e.rate-sources/items])]
    [section-inner store items]))
