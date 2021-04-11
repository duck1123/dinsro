(ns dinsro.handler
  (:require
   [dinsro.env :refer [defaults]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.layout :refer [error-page] :as layout]
   [dinsro.middleware :as middleware]
   [dinsro.routes :as routes]
   [mount.core :as mount]
   [reitit.coercion.spec]
   [reitit.ring :as ring]
   [taoensso.timbre :as timbre]))

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

(defn init-schemata
  []
  (doseq [schema db/schemata]
    (d/transact db/*conn* schema)))

(defn app []
  (timbre/info "starting app handler")
  (init-schemata)
  (middleware/wrap-base #'app-routes))
