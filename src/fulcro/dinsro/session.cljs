(ns dinsro.session
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [taoensso.timbre :as timbre]))

(defsc CurrentUser
  [_this _props]
  {:query [:user/id :user/valid?]})

(defmutation login [_]
  (action
   [{:keys [state]}]
   (timbre/info "busy"))

  (error-action
   [{:keys [state]}]
   (timbre/info "error action"))

  (ok-action
   [{:keys [state] :as env}]
   (timbre/infof "ok")
   (let [{:user/keys [id valid?]} (get-in env [:result :body `login])]
     (js/console.log id valid?)))

  (remote
   [env]
   (-> env
       (fm/returning CurrentUser)
       (fm/with-target [:session/current-user]))))
