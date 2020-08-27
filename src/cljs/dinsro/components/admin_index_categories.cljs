(ns dinsro.components.admin-index-categories
  (:require
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.create-category :as c.f.create-category]
   [dinsro.components.links :as c.links]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.spec.categories :as s.categories]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]))

(defn category-line
  [store item]
  (let [name (::s.categories/name item)
        user-id (get-in item [::s.categories/user :db/id])]
    [:tr
     [:td name]
     [:td [c.links/user-link store user-id]]
     [:td [c.buttons/delete-category store item]]]))

(defn index-categories
  [store items]
  [:<>
   [c.debug/debug-box store items]
   (if-not (seq items)
     [:p (tr [:no-categories])]
     [:table.table
      [:thead>tr
       [:th (tr [:name])]
       [:th (tr [:user])]
       [:th (tr [:actions])]]
      (into
       [:tbody]
       (for [item items] ^{:key (:db/id item)} [category-line store item]))])])

(defn section
  [store]
  (let [items @(st/subscribe store [::e.categories/items])]
    [:div.box
     [:h1
      (tr [:categories "Categories"])
      [c/show-form-button store ::e.f.create-category/shown?]]
     [c.f.create-category/form store]
     [:hr]
     (when (seq items)
       [index-categories store items])]))
