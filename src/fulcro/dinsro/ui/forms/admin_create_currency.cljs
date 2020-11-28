(ns dinsro.ui.forms.admin-create-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateCurrencyForm
  [_this _props]
  {:query []}
  (dom/div
   "Admin Create Currency"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-admin-create-currency-form (comp/factory AdminCreateCurrencyForm))
