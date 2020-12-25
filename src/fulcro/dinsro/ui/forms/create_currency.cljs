(ns dinsro.ui.forms.create-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateCurrencyForm
  [_this {:keys [close-button]}]
  {:query [{:close-button (comp/get-query u.buttons/CloseButton)}]
   :initial-state {:close-button {}}}
  (dom/div
   (u.buttons/ui-close-button close-button)
   "Create Currency form"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-create-currency-form (comp/factory CreateCurrencyForm))
