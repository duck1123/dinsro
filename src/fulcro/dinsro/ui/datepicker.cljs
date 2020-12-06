(ns dinsro.ui.datepicker
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc Datepicker
  [_this _props]
  {:query []}
  (dom/input
   :.input
   {:onSelect (fn [_] (timbre/info "on select"))}
   "Datepicker"))

(def ui-datepicker (comp/factory Datepicker))
