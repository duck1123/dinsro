(ns dinsro.ui.admin-index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc AdminIndexTransactions
  [_this _props]
  {:initial-state {}
   :query []}
  (dom/h1 "admin index transactions"))

(def ui-section (comp/factory AdminIndexTransactions))
