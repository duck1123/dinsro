(ns dinsro.handler
  (:require
   [dinsro.env :refer [defaults]]
   [datahike.api :as d]
   [dinsro.components.datahike :as db]
   [dinsro.layout :refer [error-page] :as layout]
   [dinsro.middleware :as middleware]
   [dinsro.model :as model]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.mutations.currencies :as mu.currencies]
   [dinsro.mutations.session :as mu.session]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.resolvers.categories :as r.categories]
   [dinsro.routes :as routes]
   [mount.core :as mount]
   [reitit.coercion.spec]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]
   [dinsro.mutations.accounts :as mu.accounts]))

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
  (q.currencies/create-record {::m.currencies/id "sats" ::m.currencies/name "Sats"})
  ;; (q.users/create-record #::m.users { :id "admin" })
  (try
    (mu.session/do-register "admin" "hunter2")
    (catch Exception ex
      (log/error ex "Already created")))
  (mu.currencies/do-create "usd" "Dollars" "admin")
  (mu.currencies/do-create "eur" "Euros" "admin")
  (mu.categories/do-create "admin" "Category A")
  (mu.categories/do-create "admin" "Category B")
  (mu.categories/do-create "admin" "Category C")
  (mu.accounts/do-create "savings" "sats" "admin" 0))

(defn init-schemata
  []
  (doseq [schema model/schemata]
    (d/transact db/*conn* schema)))

(defn app []
  (log/info "starting app handler")
  (init-schemata)
  (seed-db!)
  (middleware/wrap-base #'app-routes))

(comment
  (r.categories/resolve-categories))
