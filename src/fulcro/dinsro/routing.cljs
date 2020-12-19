(ns dinsro.routing
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [dinsro.app :as da]
   [dinsro.router :as router]
   [taoensso.timbre :as timbre]))

(defsc CurrentUser
  [_this _props]
  {:query [:identity]})

(defmutation finish-login [_]
  (action
   [{:keys [_app _state]}]
   (timbre/info "finish login")))

(defn start!
  []
  (dr/initialize! da/app)
  ;; TODO: parse from url
  (dr/change-route-relative! da/app router/RootRouter [""]))
