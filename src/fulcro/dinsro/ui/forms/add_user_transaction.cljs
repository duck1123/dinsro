(ns dinsro.ui.forms.add-user-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [taoensso.timbre :as timbre]))

(defsc AddUserTransactionForm
  [_this _props]
  {:query []}
  (bulma/box
   (u.buttons/ui-close-button #_close-button)

   "Add User Transaction"))

(def ui-form (comp/factory AddUserTransactionForm))
