(ns dinsro.ui.forms.login
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [dinsro.mutations]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as log]))

(defsc LoginForm
  [this {:user/keys [message password username]}]
  {:ident         (fn [] [:component/id ::form])
   :initial-state {:user/username "admin"
                   :user/message  nil
                   :user/password "hunter2"}
   :query         [:user/username :user/password :user/message]}
  (ui-form {}
    (dom/div :.is-centered
      (when message (dom/p :.notification.is-danger message))
      (ui-form-field {}
        (bulma/control
         (u.inputs/ui-text-input
          {:label "Username"
           :value username}
          {:onChange #(fm/set-string! this :user/email :event %)})))
      (ui-form-field {}
        (bulma/control
         (u.inputs/ui-text-input
          {:label "Password"
           :value password}
          {:onChange #(fm/set-string! this :user/password :event %)})))
      (bulma/field
       (bulma/control
        (u.inputs/ui-primary-button
         {:className "button"
          :content   (tr [:login])}
         {:onClick #(auth/authenticate! this :local nil)}))))))

(def ui-login-form (comp/factory LoginForm))

(defsc FormInput
  [this {:keys [my-x-val my-y-val]}]
  {:initial-state {:my-x-val "" :my-y-val ""}
   :ident         (fn [] [::id "singleton-form-input"])
   :query         [:my-x-val :my-y-val]}
  (let [on-x-change (fn [evt _] (fm/set-string! this :my-x-val :event evt))
        on-y-change (fn [evt _] (fm/set-string! this :my-y-val :event evt))]
    (dom/div {}
      (dom/p {} (str "x: " my-x-val))
      (dom/p {} (str "y: " my-y-val))
      (ui-form {:error true}
        (ui-form-input {:value my-x-val :onChange on-x-change :label "X" :error true})
        (ui-form-input {:value my-y-val :onChange on-y-change :label "Y"})))))
