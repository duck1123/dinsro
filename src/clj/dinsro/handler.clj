(ns dinsro.handler
  (:require [dinsro.env :refer [defaults]]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.layout :refer [error-page] :as layout]
            [dinsro.middleware :as middleware]
            [dinsro.model.account :as m.accounts]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.model.rates :as m.rates]
            [dinsro.model.user :as m.users]
            [dinsro.routes :as routes]
            [dinsro.spec.accounts :as s.accounts]
            [mount.core :as mount]
            [reitit.coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.webjars :refer [wrap-webjars]]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
   (ring/router routes/routes)
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
      (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

(defn init-schemata
  []
  (let [schemata [s.accounts/schema
                  m.currencies/schema
                  m.rates/schema
                  m.users/schema]]
    (doseq [schema schemata]
      (d/transact db/*conn* schema))))

;; (defn app-handler
;;   [request]
;;   (mount/)
;;   )

(defn app []
  (init-schemata)
  (middleware/wrap-base #'app-routes))
