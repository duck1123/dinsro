(ns dinsro.handler
  (:require [dinsro.env :refer [defaults]]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.layout :refer [error-page] :as layout]
            [dinsro.middleware :as middleware]
            [dinsro.routes :as routes]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.categories :as s.categories]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.spec.users :as s.users]
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
                  s.categories/schema
                  s.currencies/schema
                  s.rates/schema
                  s.transactions/schema
                  s.users/schema]]
    (doseq [schema schemata]
      (d/transact db/*conn* schema))))

(comment
  (init-schemata)
  )

;; (defn app-handler
;;   [request]
;;   (mount/)
;;   )

(defn app []
  (init-schemata)
  (middleware/wrap-base #'app-routes))
