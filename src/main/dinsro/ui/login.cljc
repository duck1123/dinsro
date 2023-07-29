(ns dinsro.ui.login
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.mutations]
   [dinsro.mutations.session :as mu.session]))

(def index-page-key :login)

(defsc IndexPage
  [this {:user/keys [message password username]} {:keys [visible?]}]
  {:ident               (fn [_] [::m.navlinks/id :login])
   :initial-state       {::m.navlinks/id :login
                         :user/username  m.users/default-username
                         :user/message   nil
                         :user/password  m.users/default-password}
   ::auth/provider      :local
   ::auth/check-session `dinsro.mutations.session/check-session
   ::auth/logout        `dinsro.mutations.session/logout
   :query               [[::auth/authorization :local]
                         ::m.navlinks/id
                         :user/username
                         :user/password
                         :user/message]
   :route-segment       ["login"]}
  (when visible?
    (dom/div :.ui.container
      (ui-form {:className "large"}
        (when message (dom/p :.notification.is-danger message))
        (ui-form-field {}
          (ui-form-input
           {:value    username
            :onChange (fn [evt _] (fm/set-string! this :user/username :event evt))
            :label    "Username"}))
        (ui-form-field {}
          (ui-form-input
           {:value    password
            :onChange (fn [evt _] (fm/set-string! this :user/password :event evt))
            :type     "password"
            :label    "Password"}))
        (ui-form-field {}
          (ui-button
           {:className "ui fluid large submit button green"
            :content   "Login"
            :onClick
            (fn [_ev]
              (let [data {:user/username username :user/password password}]
                (comp/transact! this [`(mu.session/login ~data)])))}))
        (ui-form-field {}
          (ui-button
           {:className "ui fluid large button orange"
            :content   "Cancel"
            :onClick
            (fn [_ev]
              (uism/trigger! this ::auth/auth-machine :event/cancel))}))))))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/auth-link?    true
   ::m.navlinks/label         "Login"
   ::m.navlinks/parent-key    :root
   ::m.navlinks/required-role :guest
   ::m.navlinks/router        :root})
