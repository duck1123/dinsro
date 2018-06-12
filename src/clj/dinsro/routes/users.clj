(ns dinsro.routes.users
  (:require [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
            [crypto.password.bcrypt :as bcrypt]
            [dinsro.db.core :as db]
            [dinsro.middleware.authenticated :refer [authenticated-mw]]
            [dinsro.models :as m]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(defn prepare-user
  [registration-data]
  (let [{:keys [password]} registration-data]
    (merge {:password_hash (bcrypt/encrypt password)}
           registration-data)))

(def users-routes
  (context "/api/v1/users" []
    :tags ["Users"]

    (GET "/" []
      :return [m/User]
      :summary "Index users"
      (let [users (db/list-users)]
        (ok users)))

    (GET "/:userId" []
      :return m/User
      :summary "Read User"
      :path-params [userId :- s/Int]
      (if-let [user (db/read-user {:id userId})]
        (ok user)
        (status (ok) 404)))

    (POST "/" []
      :body [registration-data m/RegistrationData]
      :summary "Create User"
      (let [user (prepare-user registration-data)]
        (db/create-user! user))
      (ok))))
