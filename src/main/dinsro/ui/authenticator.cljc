(ns dinsro.ui.authenticator
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.mutations.session :as mu.session]
   [dinsro.ui.login :as u.login]))

(auth/defauthenticator Authenticator {:local u.login/IndexPage})

(def ui-authenticator (comp/factory Authenticator))

(defsc LocalData
  [_ _]
  {:initial-state {}
   :query         [{[::auth/authorization :local] (comp/get-query mu.session/Session)}]})

(defsc UserAuthenticator
  [_ _]
  {:initial-state {:local {}}
   :query         [{:local (comp/get-query LocalData)}]})

(def my-auth-machine
  (-> auth/auth-machine

      ;; Add cancel event to gathering-credentials
      ;; transitions to idle
      (assoc-in [::uism/states :state/gathering-credentials ::uism/events :event/cancel]
                {::uism/target-state :state/idle})

      ;; Add cancel event to idle.
      ;; this just returns to idle
      (assoc-in [::uism/states :state/idle ::uism/events :event/cancel]
                {::uism/target-state :state/idle})))

(uism/register-state-machine! `auth/auth-machine my-auth-machine)
