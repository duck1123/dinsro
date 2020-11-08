(ns dinsro.ui.forms.create-category
  (:require
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.specs.events.forms.create-category :as s.e.f.create-category]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [taoensso.timbre :as timbre]))

(defn form
  [store]
  (let [form-data @(st/subscribe store [::e.f.create-category/form-data])]
    (when @(st/subscribe store [::e.f.create-category/shown?])
      [:<>
       [u/close-button store ::e.f.create-category/set-shown?]
       [u/text-input store (tr [:name]) ::s.e.f.create-category/name]
       [u/user-selector store (tr [:user]) ::s.e.f.create-category/user-id]
       [u/primary-button store (tr [:submit]) [::e.categories/do-submit form-data]]])))
