(ns dinsro.components.index-categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.links :as c.links]
   [dinsro.spec.categories :as s.categories]
   [dinsro.translations :refer [tr]]))

(defn category-line
  [item]
  (let [name (::s.categories/name item)
        user-id (get-in item [::s.categories/user :db/id])]
    [:tr
     [:td name]
     [:td [c.links/user-link user-id]]
     (c.debug/hide [:td [c.buttons/delete-category item]])]))

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
       (c.debug/hide [:th (tr [:actions])])]
      (into
       [:tbody]
       (for [item items] ^{:key (:db/id item)} [category-line item]))])])

(s/fdef index-categories
  :args (s/cat :item ::s.categories/item)
  :ret vector?)
