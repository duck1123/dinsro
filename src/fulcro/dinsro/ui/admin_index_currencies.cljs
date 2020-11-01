(ns dinsro.ui.admin-index-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc AdminIndexCurrencies
  [_this _props]
  (dom/h1 "admin index currencies"))
