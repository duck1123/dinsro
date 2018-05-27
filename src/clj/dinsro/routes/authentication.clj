(ns dinsro.routes.authentication
  (:require [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
            [dinsro.actions.authentication :refer [authenticate register]]
            [dinsro.middleware :as middleware]
            [dinsro.models :as m]))

(defn authentication-routes []

  #_(context "/api" []
    :tags ["Authentication"]

    (POST "/authenticate" []
      :summary "Authenticate"

      :body [authentication-data m/AuthenticationData]
      (authenticate authentication-data))

    (POST "/register" []
          :summary "Register"
          :body [params m/RegistrationData]
          (register))))
