(ns dinsro.ui.authenticator
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.views.login :refer [LoginPage]]))

(auth/defauthenticator Authenticator {:local LoginPage})

(def ui-authenticator (comp/factory Authenticator))
