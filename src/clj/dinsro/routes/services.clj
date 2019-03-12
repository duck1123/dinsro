(ns dinsro.routes.services
  (:require [compojure.api.sweet :refer :all]
            [dinsro.routes.authentication :refer [authentication-routes]]
            [dinsro.routes.users :refer [users-routes]]))

(def site-info
  {:version "1.0.0"
   :title "Dinsro"
   :description "Sample Services"})

(def service-routes
  (api
   {:swagger {:ui "/swagger-ui"
              :spec "/swagger.json"
              :data {:info site-info}}}

   authentication-routes
   users-routes))
