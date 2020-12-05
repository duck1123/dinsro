(ns dinsro.ui.currency-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc CurrencyAccounts
  [_this _props]
  (dom/h1 "currency accounts"))
