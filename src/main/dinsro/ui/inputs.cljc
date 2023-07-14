(ns dinsro.ui.inputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])))

(defsc PrimaryButton
  [_this _props {:keys [classes onClick]}]
  {:initial-state {}
   :query         []}
  (dom/button :.ui.button.primary.submit.fluid
    {:classes classes
     :onClick onClick} "submit"))

(def ui-primary-button (comp/computed-factory PrimaryButton))
