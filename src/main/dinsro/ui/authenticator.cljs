(ns dinsro.ui.authenticator
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.mutations.session :as mu.session]
   [dinsro.ui.login :refer [LoginPage]]))

(auth/defauthenticator Authenticator {:local LoginPage})

(def ui-authenticator (comp/factory Authenticator))

(defsc LocalData
  [_ _]
  {:initial-state {}
   :query         [{[:com.fulcrologic.rad.authorization/authorization :local] (comp/get-query mu.session/Session)}]})

(defsc UserAuthenticator
  [_ _]
  {:initial-state {:local {}}
   :query         [{:local (comp/get-query LocalData)}]})
