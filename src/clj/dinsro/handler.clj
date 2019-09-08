(ns dinsro.handler
  (:require
   [dinsro.env :refer [defaults]]
   [dinsro.layout :refer [error-page]]
            [dinsro.middleware :as middleware]
            [dinsro.routes.authentication :refer [authentication-routes]]
            [dinsro.routes.home :refer [home-routes]]
            ;; [dinsro.routes.services :refer [service-routes]]
            [dinsro.routes.user :refer [user-routes]]
            [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [dinsro.env :refer [defaults]]
            [mount.core :as mount]
            [reitit.coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
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
      ["/api/v1" {}
       (user-routes)
       (authentication-routes)]]
     {:data {:middleware [rrc/coerce-exceptions-middleware
                          rrc/coerce-request-middleware
                          rrc/coerce-response-middleware]}})
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
