(ns dinsro.components.index-categories
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.spec.categories :as s.categories]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]))

(defn-spec category-line vector?
  [item ::s.categories/item]
  (let [id (:db/id item)
        name (::s.categories/name item)
        user-id (get-in item [::s.categories/user :db/id])]
    [:tr
     [:td name]
     [:td [c.links/user-link user-id]]
     (c.debug/hide [:td [c.buttons/delete-category item]])]))

(defn-spec index-categories vector?
  [items (s/coll-of ::s.categories/item)]
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
