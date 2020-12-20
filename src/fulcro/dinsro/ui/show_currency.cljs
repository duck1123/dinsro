(ns dinsro.ui.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc ShowCurrency
  [_this {::m.currencies/keys [id name]}]
  {:query [::m.currencies/id ::m.currencies/name]
   :ident ::m.currencies/id
   :initial-state {::m.currencies/id 0
                   ::m.currencies/name ""}}
  (dom/div
   (dom/p name)
   (dom/p id)
   (ui-button {:className "button is-danger"
               :content "Delete"})))

(def ui-show-currency (comp/factory ShowCurrency))
