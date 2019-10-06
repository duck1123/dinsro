(ns dinsro.routes.authentication
  (:require [dinsro.actions.authentication :refer [authenticate register]]
            [dinsro.layout :as layout]
            [dinsro.models :as m]
            [taoensso.timbre :as timbre]))

(defn authenticate-handler
  [request]
  (let [{:keys [authentication-data]} request]
    (authenticate (assoc request :authentication-data authentication-data))))

(defn authentication-routes []
  (list
   ["/authenticate" {:post {:handler authenticate-handler
                            :summary "Authenticate"
                            ;; :body [authentication-data m/AuthenticationData]
                            }}]
   ["/register" {:post register
                 ;;           :summary "Register"
                 ;;           :body [params m/RegistrationData]
                 }]))
