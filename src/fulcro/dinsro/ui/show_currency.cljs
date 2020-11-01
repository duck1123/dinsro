(ns dinsro.ui.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]))

(defsc ShowCurrency
  [_this {:currency/keys [id name]}]
  {:query [:currency/id :currency/name]
   :ident :currency/id
   :initial-state {:currency/id 1 :currency/name "foo"}}
  (dom/div
   (dom/p "Show Currency")
   (dom/p id)
   (dom/p name)
   (dom/button "delete")))
