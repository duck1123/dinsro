(ns dinsro.ui.forms.admin-create-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateRateSourceForm
  [_this {::keys [name url currency submit-button]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::currency      {}
                   ::name          ""
                   ::submit-button {}
                   ::url           ""}
   :query [{::currency      (comp/get-query u.inputs/CurrencySelector)}
           ::name
           {::submit-button (comp/get-query u.inputs/PrimaryButton)}
           ::url]}
  (dom/div
    (u.inputs/ui-text-input
     {:label (tr [:name]) :value name})
    (u.inputs/ui-text-input
     {:label (tr [:url]) :value url})
    (u.inputs/ui-currency-selector currency)
    (bulma/field
     (bulma/control
      (u.inputs/ui-primary-button
       submit-button
       {:onClick (fn [_] (timbre/info "submit"))})))))

(def ui-admin-create-rate-source-form (comp/factory AdminCreateRateSourceForm))
