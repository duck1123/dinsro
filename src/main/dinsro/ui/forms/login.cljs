(ns dinsro.ui.forms.login
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [dinsro.mutations]
   [dinsro.mutations.session :as mu.session]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc LoginForm
  [this {:user/keys [message password username]}]
  {:ident         (fn [] [:component/id ::form])
   :initial-state {:user/message  nil
                   :user/password "hunter2"
                   :user/username "admin"}
   :query         [:user/password :user/message :user/username]}
  (dom/div
    :.is-centered
    (when message (dom/p :.notification.is-danger message))
    (bulma/field
     (bulma/control
      (u.inputs/ui-text-input
       {:label "Username"
        :value username}
       {:onChange #(fm/set-string! this :user/username :event %)})))
    (bulma/field
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
       {:onClick
        (fn []
          (let [data #:user {:username username :password password}]
            (comp/transact! this [(mu.session/login data)])))})))))

(def ui-login-form (comp/factory LoginForm))

(defsc FormInput
  [this {:keys [my-x-val my-y-val]}]
  {:initial-state {:my-x-val "" :my-y-val ""}
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
