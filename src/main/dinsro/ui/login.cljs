(ns dinsro.ui.login
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [dinsro.model.users :as m.users]
   [dinsro.mutations]
   [dinsro.mutations.session :as mu.session]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as log]))

(defsc LoginPage
  [this {:user/keys [message password username]
         :as        props}]
  {:ident               (fn [_] [:page/id ::page])
   :initial-state       {:user/username m.users/default-username
                         :user/message  nil
                         :user/password m.users/default-password}
   ::auth/provider      :local
   ::auth/check-session `dinsro.mutations.session/check-session
   ::auth/logout        `dinsro.mutations.session/logout
   :query               [[::auth/authorization :local]
                         :page/id
                         :user/username
                         :user/password
                         :user/message]
   :route-segment       ["login"]}
  (let [authorization (get props [::auth/authorization :local])
        status        (::auth/status authorization)]
    (bulma/page
     (dom/h1 :.title "Login")
     (bulma/container
      (dom/p {} (str status))
      (ui-form {}
        (dom/div :.is-centered
          (when message (dom/p :.notification.is-danger message))
          (ui-form-field {}
            (bulma/control
             (u.inputs/ui-text-input
              {:label "Username"
               :value username}
              {:onChange #(fm/set-string! this :user/username :event %)})))
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
             {:onClick
              (fn [_ev]
                (let [data {:user/username username :user/password password}]
                  (comp/transact! this [(mu.session/login data)])))})))))))))
