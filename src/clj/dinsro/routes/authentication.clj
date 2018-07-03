(ns dinsro.routes.authentication
  (:require [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
            [dinsro.actions.authentication :refer [authenticate register]]
            [dinsro.models :as m]))

(def authentication-routes
  (context "/api" []
    :tags ["Authentication"]

    (POST "/authenticate" []
          :summary "Authenticate"
          :body [authentication-data m/AuthenticationData]
          (authenticate authentication-data))

    (POST "/register" []
          :summary "Register"
          (register))))
