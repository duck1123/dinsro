(ns dinsro.routes.users
  (:require [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
            [dinsro.actions.user.create-user :refer [create-user-response]]
            [dinsro.actions.user.list-user :refer [list-user-response]]
            [dinsro.actions.user.read-user :refer [read-user-response]]
            [dinsro.middleware.authenticated :refer [authenticated-mw]]
            [dinsro.models :as m]
            [schema.core :as s]))

(def users-routes
  (context "/api/v1/users" []
    :tags ["Users"]

    (GET "/" []
      :return [m/User]
      :summary "Index users"
      (list-user-response))

    (GET "/:userId" []
      :return m/User
      :summary "Read User"
      :path-params [userId :- s/Int]
      (read-user-response userId))

    (POST "/" []
      :body [registration-data m/RegistrationData]
      :summary "Create User"
      (create-user-response registration-data))))
