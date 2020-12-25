(ns dinsro.ui.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc ShowCurrency
  [_this {:currency/keys [id name]}]
  {:query [:currency/id :currency/name]
   :ident :currency/id
   :initial-state {:currency/id 1
                   :currency/name ""}}
  (dom/div
   (dom/p name)
   (dom/p id)
   (ui-button {:className "button is-danger"
               :content "Delete"})))
