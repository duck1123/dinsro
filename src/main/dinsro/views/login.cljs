(ns dinsro.views.login
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.forms.login :as u.f.login]
   [taoensso.timbre :as log]))

(defsc LoginPage
  [_this {::keys [form]}]
  {:ident               (fn [_] [:page/id ::page])
   :initial-state       {::form {}}
   ::auth/provider      :local
   ::auth/check-session `dinsro.mutations.session/check-session
   ::auth/logout        `dinsro.mutations.session/logout
   :query               [:page/id
                         {::form (comp/get-query u.f.login/LoginForm)}]
   :route-segment       ["login"]}
  (bulma/page
   (dom/h1 :.title "Login")
   (bulma/container
    (u.f.login/ui-login-form form))))
