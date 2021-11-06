(ns dinsro.ui.login
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [dinsro.model.users :as m.users]
   [dinsro.mutations]
   [dinsro.mutations.session :as mu.session]
   [dinsro.translations :refer [tr]]
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
    (dom/div :.ui.middle.aligned.center.aligned.grid
      (dom/div :.column
        (dom/h1 :.ui.header (tr [:login]))
        (dom/p {} (str status))
        (when message (dom/p :.notification.is-danger message))
        (ui-form {:className "large"}
          (dom/div :.ui.stacked.segment
            (ui-form-field {}
              (ui-form-input
               {:value    username
                :onChange (fn [evt _] (fm/set-string! this :user/username :event evt))
                :label    (tr [:username])}))
            (ui-form-field {}
              (ui-form-input
               {:value    password
                :onChange (fn [evt _] (fm/set-string! this :user/password :event evt))
                :label    (tr [:password])}))
            (ui-form-field {}
              (u.inputs/ui-primary-button
               {:className "ui fluid large submit button"
                :content   (tr [:login])}
               {:onClick
                (fn [_ev]
                  (let [data {:user/username username :user/password password}]
                    (comp/transact! this [(mu.session/login data)])))}))))))))
