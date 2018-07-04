(ns dinsro.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(def site-info
  {:version "1.0.0"
   :title "Dinsro"
   :description "Sample Services"})

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info site-info}}})
