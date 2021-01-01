(ns dinsro.ui.forms.admin-create-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateRateSourceForm
  [_this _props]
  {:initial-state {}
   :query []}
  (bulma/box
   (u.inputs/ui-text-input)
   (u.inputs/ui-text-input)
   (u.inputs/ui-currency-selector)
   (bulma/field
    (bulma/control
     (u.inputs/ui-primary-button)))

   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-admin-create-rate-source-form (comp/factory AdminCreateRateSourceForm))
