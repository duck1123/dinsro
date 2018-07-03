(ns dinsro.routes.authentication
  (:require [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
            [dinsro.actions.authentication.authenticate :refer [authenticate]]
            [dinsro.models :as m]
            [ring.util.http-response :refer :all]))

(def authentication-routes
  (context "/api" []
    :tags ["Authentication"]

    (POST "/authenticate" []
          :summary "Authenticate"
          :body [authentication-data m/AuthenticationData]
          (authenticate authentication-data))

    (POST "/register" []
          :summary "Register"
          (ok))))
