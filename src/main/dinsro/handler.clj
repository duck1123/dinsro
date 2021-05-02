(ns dinsro.handler
  (:require
   [dinsro.env :refer [defaults]]
   [datahike.api :as d]
   [dinsro.components.datahike :as db]
   [dinsro.layout :refer [error-page] :as layout]
   [dinsro.middleware :as middleware]
   [dinsro.model :as model]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.queries.currencies :as q.currencies]
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

(defn seed-db!
  []
  (q.currencies/create-record {::m.currencies/id "sats" ::m.currencies/name "Sats"}))

(defn init-schemata
  []
  (doseq [schema model/schemata]
    (d/transact db/*conn* schema)))

(defn app []
  (timbre/info "starting app handler")
  (init-schemata)
  (seed-db!)
  (middleware/wrap-base #'app-routes))
