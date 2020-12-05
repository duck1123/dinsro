(ns dinsro.views.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc IndexRatesPage
  [_this _props]
  (dom/div "Index rates"))

(def ui-page (comp/factory IndexRatesPage))
