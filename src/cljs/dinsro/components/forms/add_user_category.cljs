(ns dinsro.components.forms.add-user-category
  (:require
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.add-user-category :as e.f.add-user-category]
   [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
   [dinsro.translations :refer [tr]]
   [re-frame.core :as rf]))

(defn form
  [user-id]
  (let [form-data (assoc @(rf/subscribe [::e.f.add-user-category/form-data]) :user-id user-id)]
    (when @(rf/subscribe [::e.f.add-user-category/shown?])
      [:div
       [c/close-button ::e.f.add-user-category/set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::s.e.f.create-category/name]
       [c/primary-button (tr [:submit]) [::e.categories/do-submit form-data]]])))
