(ns dinsro.routes.authentication
  (:require [dinsro.actions.authentication :refer [authenticate register]]
            [dinsro.layout :as layout]
            ;; [dinsro.middleware :as middleware]
            [dinsro.models :as m]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as rrc]
            [reitit.ring :as ring]))

(defn authenticate-handler
  [request]
  (println "authenticate")
  (layout/render "home.html"))

(defn authentication-routes []
  (list
   ["/authenticate" {:post {:handler authenticate-handler
                            ;; :body [authentication-data m/AuthenticationData]
                            }}]
   ["/register" {:post register
                 ;;           :summary "Register"
                 ;;           :body [params m/RegistrationData]
                 }]))
