(ns dinsro.ui.forms.add-category-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddCategoryTransactionForm
  [_this {::keys [description value]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::description {}
                   ::input       {}
                   ::value       {}}
   :query [{::description (comp/get-query u.inputs/TextInput)}
           {::input (comp/get-query u.inputs/TextInput)}
           {::value (comp/get-query u.inputs/TextInput)}]}
  (bulma/box
   (bulma/field-group
    (bulma/field
     (bulma/column
      (dom/label :.label (tr [:value]))
      (u.inputs/ui-text-input value)))
    (bulma/field
     (bulma/column
      (u.inputs/ui-text-input description))))))

(def ui-form (comp/factory AddCategoryTransactionForm))
