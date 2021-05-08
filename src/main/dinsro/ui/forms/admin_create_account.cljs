(ns dinsro.ui.forms.admin-create-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateAccountForm
  [this {::keys [currency initial-value name user]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::currency      {}
                   ::initial-value ""
                   ::name          ""
                   ::user          {}}
   :query [{::currency     (comp/get-query u.inputs/CurrencySelector)}
           ::initial-value
           ::name
           {::user         (comp/get-query u.inputs/UserSelector)}]}
  (dom/div
    (u.inputs/ui-text-input
     {:label (tr [:name]) :value name}
     {:onChange #(fm/set-string! this ::name :event %)})
    (u.inputs/ui-text-input
     {:label (tr [:initial-value]) :value initial-value}
     {:onChange #(fm/set-string! this ::initial-value :event %)})
    (u.inputs/ui-currency-selector currency)
    (u.inputs/ui-user-selector user)))

(def ui-admin-create-account-form (comp/factory AdminCreateAccountForm))
