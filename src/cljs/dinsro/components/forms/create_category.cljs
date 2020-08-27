(ns dinsro.components.forms.create-category
  (:require
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn form
  [store]
  (let [form-data @(st/subscribe store [::e.f.create-category/form-data])]
    (when @(st/subscribe store [::e.f.create-category/shown?])
      [:<>
       [c/close-button store ::e.f.create-category/set-shown?]
       [c.debug/debug-box store form-data]
       [c/text-input store (tr [:name]) ::s.e.f.create-category/name]
       [c/user-selector store (tr [:user]) ::s.e.f.create-category/user-id]
       [c/primary-button store (tr [:submit]) [::e.categories/do-submit form-data]]])))
