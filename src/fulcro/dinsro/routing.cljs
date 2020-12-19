(ns dinsro.routing
  (:require
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.app :as da]
   [dinsro.router :as router]
   [taoensso.timbre :as timbre]))

(defn start!
  []
  (dr/initialize! da/app)
  ;; TODO: parse from url
  (dr/change-route-relative! da/app router/RootRouter [""]))
