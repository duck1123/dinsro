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
  {:query         [{[:com.fulcrologic.rad.authorization/authorization :local] (comp/get-query mu.session/Session)}]
   :initial-state {}})

(defsc UserAuthenticator
  [_ _]
  {:query         [{:local (comp/get-query LocalData)}]
   :initial-state {:local {}}})
