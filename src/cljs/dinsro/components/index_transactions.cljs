(ns dinsro.components.index-transactions
  (:require [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]))

(defn-spec row-line vector?
  [transaction ::s.transactions/item]
  [:tr
   [:td (:db/id transaction)]
   [:td (::s.transactions/value transaction)]
   [:td [c.links/currency-link (:db/id (::s.transactions/currency transaction))]]
   [:td [c.links/account-link (:db/id (::s.transactions/account transaction))]]
   [:td [:a.button "Click"]]])

(defn index-transactions
  [items]
  [:div "Index transactions"]
  (if-not (seq items)
    [:p "no items"]
    [:<>
     [c.debug/debug-box items]
     [:table.table
      [:thead>tr
       [:th "Id"]
       [:th "Value"]
       [:th "Currency"]
       [:th "Account"]
       [:th "Buttons"]]
      (into
       [:tbody]
       (for [transaction items]
         ^{:key (:db/id transaction)}
         [row-line transaction]))]]))
