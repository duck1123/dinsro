(ns dinsro.handler
  (:require
   [dinsro.env :refer [defaults]]
   [dinsro.layout :refer [error-page]]
            [dinsro.middleware :as middleware]
            [dinsro.routes.authentication :refer [authentication-routes]]
            [dinsro.routes.home :refer [home-routes]]
            [dinsro.routes.services :refer [service-routes]]
            [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [dinsro.env :refer [defaults]]
            [mount.core :as mount]
            [reitit.ring :as ring]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.webjars :refer [wrap-webjars]]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app
  :start
  (middleware/wrap-base
   (ring/ring-handler
    (ring/router
     [(home-routes)
      (authentication-routes)])
    (ring/routes
     (ring/create-resource-handler
      {:path "/"})
     (wrap-content-type
      (wrap-webjars (constantly nil)))
     (ring/create-default-handler
      {:not-found
       (constantly (error-page {:status 404, :title "404 - Page not found"}))
       :method-not-allowed
       (constantly (error-page {:status 405, :title "405 - Not allowed"}))
       :not-acceptable
       (constantly (error-page {:status 406, :title "406 - Not acceptable"}))})))))
