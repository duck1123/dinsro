(ns dinsro.handler
  (:require
            [dinsro.layout :refer [error-page]]
            [dinsro.routes.home :refer [home-routes]]
            [dinsro.routes.services :refer [service-routes]]
            [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [dinsro.env :refer [defaults]]
            [mount.core :as mount]
            [dinsro.middleware :as middleware]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(mount/defstate app
  :start
  (middleware/wrap-base
    (routes
      (-> #'home-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-formats))
      (-> #'service-routes
          (wrap-routes middleware/wrap-restricted))
      (route/not-found
       (:body
        (error-page {:status 404
                     :title "page not found"}))))))
