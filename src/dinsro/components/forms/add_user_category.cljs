(ns dinsro.components.forms.add-user-category
  (:require
   [dinsro.components :as c]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.add-user-category :as e.f.add-user-category]
   [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]))

(defn form
  [store user-id]
  (let [form-data (assoc @(st/subscribe store [::e.f.add-user-category/form-data]) :user-id user-id)]
    (when @(st/subscribe store [::e.f.add-user-category/shown?])
      [:div
       [c/close-button store ::e.f.add-user-category/set-shown?]
       [c/text-input store (tr [:name]) ::s.e.f.create-category/name]
       [c/primary-button store (tr [:submit]) [::e.categories/do-submit form-data]]])))
