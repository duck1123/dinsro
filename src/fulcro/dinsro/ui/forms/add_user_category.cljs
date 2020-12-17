(ns dinsro.ui.forms.add-user-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddUserCategoryForm
  [_this _props]
  {:query []}
  (bulma/box
   (u.buttons/ui-close-button #_close-button)
   (u.inputs/ui-text-input)
   (u.inputs/ui-primary-button)))

(def ui-form (comp/factory AddUserCategoryForm))
