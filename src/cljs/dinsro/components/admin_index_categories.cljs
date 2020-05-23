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
   [dinsro.translations :refer [tr]]
   [re-frame.core :as rf]))

(defn category-line
  [item]
  (let [name (::s.categories/name item)
        user-id (get-in item [::s.categories/user :db/id])]
    [:tr
     [:td name]
     [:td [c.links/user-link user-id]]
     [:td [c.buttons/delete-category item]]]))

(defn index-categories
  [items]
  [:<>
   [c.debug/debug-box items]
   (if-not (seq items)
     [:p (tr [:no-categories])]
     [:table.table
      [:thead>tr
       [:th (tr [:name])]
       [:th (tr [:user])]
       [:th (tr [:actions])]]
      (into
       [:tbody]
       (for [item items] ^{:key (:db/id item)} [category-line item]))])])

(defn section
  []
  (let [items @(rf/subscribe [::e.categories/items])]
    [:div.box
     [:h1
      (tr [:categories "Categories"])
      [c/show-form-button ::e.f.create-category/shown?]]
     [c.f.create-category/form]
     [:hr]
     (when (seq items)
       [index-categories items])]))
