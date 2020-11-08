(ns dinsro.ui.admin-index-rate-sources
  (:require
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.forms.create-rate-source :as e.f.create-rate-sources]
   [dinsro.specs.rate-sources :as s.rate-sources]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-rate-source :as u.f.create-rate-source]
   [dinsro.ui.links :as u.links]))

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
     [:td [u.links/currency-link store currency-id]]
     [:td
      [:button.button
       {:on-click #(st/dispatch store [::e.rate-sources/do-run-source id])}
       "Run"]
      [u.buttons/delete-rate store item]]]))

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

(defn section
  [store]
  (let [items @(st/subscribe store [::e.rate-sources/items])]
    [:div.box
     [:h2.title.is-2
      (tr [:rate-sources])
      [u/show-form-button store ::e.f.create-rate-sources/shown?]]
     [u.f.create-rate-source/form store]
     (rate-sources-table store items)]))
