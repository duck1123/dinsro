(ns dinsro.views.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc ShowCurrencyPage
  [_this _props]
  {:initial-state {}}
  (dom/div "Show Currency Page"))
