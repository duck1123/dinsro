(ns dinsro.ui.forms.add-user-account
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.specs :as ds]
   [dinsro.specs.events.forms.create-account :as s.e.f.create-account]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]))

(defn form
  [store id]
  (let [form-data (assoc @(st/subscribe store [::e.f.add-user-account/form-data]) :user-id id)]
    (when @(st/subscribe store [::e.f.add-user-account/shown?])
      [:<>
       [u/close-button store ::e.f.add-user-account/set-shown?]
       [:div.field>div.control
        [u/text-input store (tr [:name]) ::s.e.f.create-account/name]]
       [:div.field>div.control
        [u/number-input store (tr [:initial-value]) ::s.e.f.create-account/initial-value]]
       [:div.field>div.control
        [u/currency-selector store (tr [:currency]) ::s.e.f.create-account/currency-id]]
       [:div.field>div.control
        [u/primary-button store (tr [:submit]) [::e.accounts/do-submit form-data]]]])))

(s/fdef form
  :args (s/cat :store #(instance? st/Store %)
               :id ::ds/id)
  :ret vector?)