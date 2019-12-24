(ns dinsro.components.index-transactions
  (:require [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]))

(defn-spec row-line vector?
  [transaction ::s.transactions/item]
  [:tr
   (c.debug/hide [:td (:db/id transaction)])
   [:td (str (::s.transactions/date transaction))]
   [:td (::s.transactions/value transaction)]
   [:td [c.links/currency-link (:db/id (::s.transactions/currency transaction))]]
   [:td [c.links/account-link (:db/id (::s.transactions/account transaction))]]
   (c.debug/hide [:td [c.buttons/delete-transaction transaction]])])

(defn-spec index-transactions vector?
  [items (s/coll-of ::s.transactions/item)]
  [:div "Index transactions"]
  (if-not (seq items)
    [:p "no items"]
    [:<>
     [c.debug/debug-box items]
     [:table.table
      [:thead>tr
       (c.debug/hide [:th "Id"])
       [:th "Date"]
       [:th "Value"]
       [:th "Currency"]
       [:th "Account"]
       (c.debug/hide [:th (tr [:actions])])]
      (into
       [:tbody]
       (let [items (map-indexed
                    (fn [i ])
                    items)]
         (for [transaction items]
                 ^{:key (:db/id transaction)}
                 [row-line transaction])))]]))
