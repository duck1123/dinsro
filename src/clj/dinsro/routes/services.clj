(ns dinsro.routes.services
  (:require [dinsro.db.core :as db]
            [dinsro.models :as m]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

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
      (if-let [user (db/get-user {:id "0"})]
        (ok [user])
        (ok [])))))
