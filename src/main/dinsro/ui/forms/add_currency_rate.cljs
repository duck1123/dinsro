(ns dinsro.ui.forms.add-currency-rate
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc AddCurrencyRateForm
  [_this _props]
  {:ident (fn [] [:component/id ::form])
   :initial-state {}
   :query [:component/id]}
  (dom/div "Add Currency Rate"))

(def ui-form (comp/factory AddCurrencyRateForm))
