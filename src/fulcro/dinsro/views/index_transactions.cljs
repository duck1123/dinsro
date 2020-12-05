(ns dinsro.views.index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc IndexTransactionsPage
  [_this _props]
  (dom/div "Index transactions"))

(def ui-page (comp/factory IndexTransactionsPage))
