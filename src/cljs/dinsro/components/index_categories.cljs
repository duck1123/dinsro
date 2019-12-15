(ns dinsro.components.index-categories
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.categories :as e.categories]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.categories :as s.categories]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn-spec category-line vector?
  [item ::s.categories/item]
  (let [id (:db/id item)
        name (::s.categories/name item)
        user-id (get-in item [::s.categories/user :db/id])]
    [:tr
     [:td name]
     [:td [c.links/user-link user-id]]
     #_(c.debug/hide [:td [c.buttons/delete-category item]])]))

(defn-spec index-categories vector?
  [items (s/coll-of any? #_::s.categories/item)]
  [:<>
   [c.debug/debug-box items]
   (if-not (seq items)
     [:p (tr [:no-categories])]
     [:table.table
      [:thead>tr
       [:th (tr [:name])]
       [:th (tr [:user])]
       #_(c.debug/hide [:th (tr [:actions])])]
      (->> (for [item items] ^{:key (:db/id item)} [category-line item])
           (into [:tbody]))])])
