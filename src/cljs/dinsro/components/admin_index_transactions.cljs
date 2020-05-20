(ns dinsro.components.admin-index-transactions
  (:require
   [dinsro.events.transactions :as e.transactions]
   [re-frame.core :as rf]))

(defn index-transactions
  [items]
  [:div])

(defn section
  []
  [:div.box
   [:h1 "Index Transactions"]
   (let [items @(rf/subscribe [::e.transactions/items])]
     (when (seq items)
       [index-transactions items]))])
