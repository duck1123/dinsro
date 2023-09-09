(ns dinsro.ui.forms.admin.users.categories
  (:require
   [com.fulcrologic.fulcro.components :as comp  :refer [defsc]]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.categories :as m.categories]
   [dinsro.mutations.categories :as mu.categories]
   [lambdaisland.glogc :as log]))

(def create!-form-button
  {:type   :button
   :local? true
   :label  "Create"
   :action (fn [this _]
             (let [props (comp/props this)]
               (comp/transact! this [`(mu.categories/create! ~props)])))})

(defsc NewForm
  [this {::m.categories/keys [name]
         :as                 props}]
  {:ident         (fn [] [:component/id ::NewForm])
   :initial-state {::m.categories/name ""}
   :query         [::m.categories/name]}
  (log/debug :NewForm/starting {:props props})
  (ui-segment {}
    (ui-form {}
      (ui-form-field {}
        (ui-form-input
         {:value    name
          :onChange (fn [evt _] (fm/set-string! this ::m.categories/name :event evt))
          :label    "Name"}))
      (ui-form-field {}
        (ui-button
         {:content "Submit"
          :primary true
          :fluid   true
          :size    "large"
          :onClick
          (fn [_ev]
            (comp/transact! this [`(mu.categories/create! {::m.categories/name ~name})]))})))))

(def ui-new-form (comp/factory NewForm))

(form/defsc-form NewForm2
  [_this _props]
  {fo/action-buttons [::create!]
   fo/attributes     [m.categories/name]
   fo/controls       {::create! create!-form-button}
   fo/id             m.categories/id
   fo/route-prefix   "new-category"
   fo/title          "Create Category"})
