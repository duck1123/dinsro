(ns dinsro.handler
  (:require
   [dinsro.layout :refer [error-page] :as layout]
   [dinsro.middleware :as middleware]
   [dinsro.seed :as seed]
   [mount.core :as mount]
   [reitit.coercion.spec]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]))

(mount/defstate app-routes
  :start
  (ring/ring-handler
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
