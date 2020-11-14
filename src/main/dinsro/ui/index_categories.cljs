(ns dinsro.ui.index-categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]))

(defn category-line
  [store item]
  (let [name (::m.categories/name item)
        user-id (get-in item [::m.categories/user :db/id])]
    [:tr
     [:td name]
     [:td [u.links/user-link store user-id]]
     (u.debug/hide store [:td [u.buttons/delete-category store item]])]))

(defn index-categories
  [store items]
  (if-not (seq items)
    [:p (tr [:no-categories])]
    [:table.table
     [:thead>tr
      [:th (tr [:name])]
      [:th (tr [:user])]
      (u.debug/hide store [:th (tr [:actions])])]
     (into
      [:tbody]
      (for [item items] ^{:key (:db/id item)} [category-line store item]))]))

(s/fdef index-categories
  :args (s/cat :item ::m.categories/item)
  :ret vector?)
