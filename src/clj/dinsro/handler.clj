(ns dinsro.handler
  (:require [dinsro.layout :refer [error-page]]
            [dinsro.middleware :as middleware]
            [dinsro.routes.authentication :refer [authentication-routes]]
            [dinsro.routes.home :refer [home-routes]]
            [dinsro.routes.user :refer [user-routes]]
            [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [dinsro.env :refer [defaults]]
            [mount.core :as mount]
            [reitit.coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
   (ring/router
    [(home-routes)
     ["/api/v1" {}
      (user-routes)
      (authentication-routes)]])
   (ring/routes
    (ring/create-resource-handler
     {:path "/"})
    (ring/create-default-handler
     {:not-found
      (constantly (error-page {:status 404, :title "404 - Page not found"}))
      :method-not-allowed
      (constantly (error-page {:status 405, :title "405 - Not allowed"}))
      :not-acceptable
      (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

(defn app []
  (middleware/wrap-base #'app-routes))
