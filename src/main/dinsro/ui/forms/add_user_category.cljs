(ns dinsro.ui.forms.add-user-category
  (:require
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.add-user-category :as e.f.add-user-category]
   [dinsro.specs.events.forms.create-category :as s.e.f.create-category]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]))

(defn form
  [store user-id]
  (let [form-data (assoc @(st/subscribe store [::e.f.add-user-category/form-data]) :user-id user-id)]
    (when @(st/subscribe store [::e.f.add-user-category/shown?])
      [:div
       [u.buttons/close-button store ::e.f.add-user-category/set-shown?]
       [u.inputs/text-input store (tr [:name]) ::s.e.f.create-category/name]
       [u.inputs/primary-button store (tr [:submit]) [::e.categories/do-submit form-data]]])))
