(ns dinsro.components.forms.create-category
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.categories :as e.categories]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn form
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:<>
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::name ::set-name]
       [c/user-selector (tr [:user]) ::user-id ::set-user-id]
       [c/primary-button (tr [:submit]) [::e.categories/do-submit form-data]]])))
