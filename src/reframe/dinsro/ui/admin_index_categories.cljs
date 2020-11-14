(ns dinsro.ui.admin-index-categories
  (:require
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.model.categories :as m.categories]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-category :as u.f.create-category]
   [dinsro.ui.links :as u.links]))

(defn category-line
  [store item]
  (let [name (::m.categories/name item)
        user-id (get-in item [::m.categories/user :db/id])]
    [:tr
     [:td name]
     [:td [u.links/user-link store user-id]]
     [:td [u.buttons/delete-category store item]]]))

(defn index-categories
  [store items]
  (if-not (seq items)
    [:p (tr [:no-categories])]
    [:table.table
     [:thead>tr
      [:th (tr [:name])]
      [:th (tr [:user])]
      [:th (tr [:actions])]]
     (into
      [:tbody]
      (for [item items] ^{:key (:db/id item)} [category-line store item]))]))

(defn section
  [store]
  (let [items @(st/subscribe store [::e.categories/items])]
    [:div.box
     [:h1
      (tr [:categories "Categories"])
      [u.buttons/show-form-button store ::e.f.create-category/shown?]]
     [u.f.create-category/form store]
     [:hr]
     (when (seq items)
       [index-categories store items])]))
