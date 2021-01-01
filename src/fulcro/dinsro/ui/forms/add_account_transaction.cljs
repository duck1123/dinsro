(ns dinsro.ui.forms.add-account-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.datepicker :as u.datepicker]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddAccountTransactionForm
  [_this _props]
  {:query []}
  (dom/div
   (bulma/field
    (bulma/control
     (u.inputs/ui-text-input)))
   (bulma/field
    (bulma/control
     (u.inputs/ui-number-input)))
   (bulma/field
    (bulma/control
     (u.datepicker/ui-datepicker)))
   (bulma/field
    (bulma/control
     (u.inputs/ui-primary-button)))))

(def ui-add-account-transaction-form
  (comp/factory AddAccountTransactionForm))
