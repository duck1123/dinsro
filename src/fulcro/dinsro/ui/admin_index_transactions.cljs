(ns dinsro.ui.admin-index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc AdminIndexTransactions
  [_this _props]
  (dom/h1 "admin index transactions"))
