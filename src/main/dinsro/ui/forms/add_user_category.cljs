(ns dinsro.ui.forms.add-user-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.model.categories :as m.categories]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddUserCategoryForm
  [this {::keys [name]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::name ""}
   :query [::name]}
  (dom/div
   (u.inputs/ui-text-input
    {:label (tr [:name]) :value name}
    {:onChange #(fm/set-string! this ::name :event %)})
   (u.inputs/ui-primary-button
    {}
    {:onClick
     #(comp/transact! this [(mu.categories/create! {::m.categories/name name})])})))

(def ui-form (comp/factory AddUserCategoryForm))
