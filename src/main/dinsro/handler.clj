(ns dinsro.handler
  (:require
   [dinsro.env :refer [defaults]]
   [dinsro.layout :refer [error-page] :as layout]
   [dinsro.middleware :as middleware]
   [dinsro.routes :as routes]
   [dinsro.seed :as seed]
   [mount.core :as mount]
   [reitit.coercion.spec]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
   (ring/router routes/routes)
   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (ring/create-default-handler
     {:not-found
      (constantly (error-page {:status 404, :title "404 - Page not found"}))
      :method-not-allowed
      (constantly (error-page {:status 405, :title "405 - Not allowed"}))
      :not-acceptable
      (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

(defn app []
  (log/info "starting app handler")
  (seed/seed-db!)
  (middleware/wrap-base #'app-routes))
