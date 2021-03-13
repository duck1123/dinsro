(ns dinsro.session
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [dinsro.routing :as routing]
   [taoensso.timbre :as timbre]))

(defsc CurrentUser
  [_this _props]
  {:query [:user/id :user/valid?]})

(defmutation finish-login [_]
  (action
   [{:keys [_app state]}]
   (let [logged-in? (get-in @state [:session/current-user :user/valid?])]
     (when-not logged-in?
       (routing/route-to! "/login"))
     (swap! state #(assoc % :root/ready? true)))))

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
   (let [{:user/keys [valid?]} (get-in env [:result :body `login])]
     (when-not valid?
       (swap! state #(assoc-in % [:component/id :dinsro.ui.forms.login/form :user/message]
                               "Can't log in")))))

  (remote
   [env]
   (-> env
       (fm/returning CurrentUser)
       (fm/with-target [:session/current-user]))))
