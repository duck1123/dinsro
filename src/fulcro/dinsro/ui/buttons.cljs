(ns dinsro.ui.buttons
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc ShowFormButton
  [_this _props]
  {:query []}
  (dom/button "+"))

(def ui-show-form-button (comp/factory ShowFormButton))

(defsc DeleteButton
  [_this _props]
  {:query []}
  (dom/button :.button.is-danger "Delete"))

(def ui-delete-button (comp/factory DeleteButton))
