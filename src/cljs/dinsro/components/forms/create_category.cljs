(ns dinsro.components.forms.create-category
  (:require
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
   [dinsro.translations :refer [tr]]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(defn form
  []
  (let [form-data @(rf/subscribe [::e.f.create-category/form-data])]
    (when @(rf/subscribe [::e.f.create-category/shown?])
      [:<>
       [c/close-button ::e.f.create-category/set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::s.e.f.create-category/name]
       [c/user-selector (tr [:user]) ::s.e.f.create-category/user-id]
       [c/primary-button (tr [:submit]) [::e.categories/do-submit form-data]]])))
