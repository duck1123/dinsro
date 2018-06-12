(ns dinsro.routes.services
  (:require [dinsro.db.core :as db]
            [dinsro.models :as m]
            [dinsro.routes.users :refer [users-routes]]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(def site-info
  {:version "1.0.0"
   :title "Dinsro"
   :description "Sample Services"})

(def service-routes
  (api
   {:swagger {:ui "/swagger-ui"
              :spec "/swagger.json"
              :data {:info site-info}}}

   (context "/api" []
     :tags ["Authentication"]

     (POST "/authenticate" []
           :summary "Authenticate"
           :body [authentication-data m/AuthenticationData]
           (let [{:keys [email password]} authentication-data]
             (ok)))

     (POST "/register" []
           :summary "Register"
           (ok)))

   users-routes))
