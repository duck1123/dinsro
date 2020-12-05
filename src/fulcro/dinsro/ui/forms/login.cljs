(ns dinsro.ui.forms.login
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc LoginForm
  [this {:keys [my-x-val my-y-val]}]
  {:initial-state (fn [_] {:my-x-val "" :my-y-val ""})
   :ident         (fn [] [::id "singleton-form-input"])
   :query         [:my-x-val :my-y-val]}
  (let [on-x-change (fn [evt _] (fm/set-string! this :my-x-val :event evt))
        on-y-change (fn [evt _] (fm/set-string! this :my-y-val :event evt))]
    (ui-form
     {:error true}
     (ui-form-input {:label "Email" :value my-x-val :onChange on-x-change})
     (ui-form-input {:label "Password" :value my-y-val :onChange on-y-change})
     (ui-button {:className "button"
                 :content (tr [:login])}))))

(defsc FormInput
  [this {:keys [my-x-val my-y-val]}]
  {:initial-state (fn [_] {:my-x-val "" :my-y-val ""})
   :ident         (fn [] [::id "singleton-form-input"])
   :query         [:my-x-val :my-y-val]}
  (let [on-x-change (fn [evt _] (fm/set-string! this :my-x-val :event evt))
        on-y-change (fn [evt _] (fm/set-string! this :my-y-val :event evt))]
    (dom/div
     (dom/p {} (str "x: " my-x-val))
     (dom/p {} (str "y: " my-y-val))
     (ui-form
      {:error true}
      (ui-form-input {:value my-x-val :onChange on-x-change :label "X" :error true})
      (ui-form-input {:value my-y-val :onChange on-y-change :label "Y"})))))
