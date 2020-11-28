(ns dinsro.ui.forms.admin-create-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateAccountForm
  [_this _props]
  {:initial-state {}
   :query []}
  (dom/div
   "Admin Create Account"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-admin-create-account-form (comp/factory AdminCreateAccountForm))
