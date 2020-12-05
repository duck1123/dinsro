(ns dinsro.handler
  (:require
   [dinsro.env :refer [defaults]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.layout :refer [error-page] :as layout]
   [dinsro.middleware :as middleware]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.routes :as routes]
   [mount.core :as mount]
   [reitit.coercion.spec]
   [reitit.ring :as ring]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.webjars :refer [wrap-webjars]]
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
  (let [schemata [m.accounts/schema
                  m.categories/schema
                  m.currencies/schema
                  m.rates/schema
                  m.rate-sources/schema
                  m.transactions/schema
                  m.users/schema]]
    (doseq [schema schemata]
      (d/transact db/*conn* schema))))

(defn app []
  (timbre/info "starting app")
  (init-schemata)
  (middleware/wrap-base #'app-routes))
