(ns dinsro.routes.services
  (:require [dinsro.db.core :as db]
            [dinsro.models :as m]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(defn prepare-user
  [registration-data]
  (merge {:password_hash ""}
         registration-data))

(def site-info
  {:version "1.0.0"
   :title "Dinsro"
   :description "Sample Services"})

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info site-info}}}

  (context "/api/users" []
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
