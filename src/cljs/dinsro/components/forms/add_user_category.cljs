(ns dinsro.components.forms.add-user-category
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.categories :as e.categories]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]))

(defn form
  [user-id]
  (let [form-data (assoc @(rf/subscribe [::form-data]) :user-id user-id)]
    (when @(rf/subscribe [::shown?])
      [:div
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::name ::set-name]
       [c/primary-button (tr [:submit]) [::e.categories/do-submit form-data]]])))
