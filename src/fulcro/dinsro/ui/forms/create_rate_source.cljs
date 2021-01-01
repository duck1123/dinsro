(ns dinsro.ui.forms.create-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateRateSourceForm
  [_this _props]
  {:query []
   :initial-state {}}
  (bulma/box
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-create-rate-source-form (comp/factory CreateRateSourceForm))
