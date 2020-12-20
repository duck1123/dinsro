(ns dinsro.session
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [taoensso.timbre :as timbre]))

(defsc CurrentUser
  [_this _props]
  {:query [:identity]})

(defmutation login [_]
  (action
   [{:keys [state]}]
   (timbre/info "busy"))

  (error-action
   [{:keys [state]}]
   (timbre/info "error action"))

  (ok-action
   [{:keys [state]}]
   (timbre/info "ok"))

  (remote
   [env]
   (-> env
       (fm/returning CurrentUser)
       (fm/with-target [:session/current-user]))))
