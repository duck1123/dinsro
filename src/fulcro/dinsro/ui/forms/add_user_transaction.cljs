(ns dinsro.ui.forms.add-user-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as timbre]))

(defsc AddUserTransactionForm
  [_this _props]
  {:query []}
  (bulma/box
   "Add User Transaction"))

(def ui-form (comp/factory AddUserTransactionForm))
