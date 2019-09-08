(ns dinsro.routes.user
  (:require [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
            [dinsro.actions.user.create-user :refer [create-user-response]]
            [dinsro.actions.user.list-user :refer [list-user-response]]
            [dinsro.actions.user.read-user :refer [read-user-response]]
            [dinsro.middleware.authenticated :refer [authenticated-mw]]
            [dinsro.models :as m]
            [ring.util.http-response :as response]
            [schema.core :as s]))

(defn list-user-handler
  [request]
  (list-user-response))

(defn read-user-handler
  [request]
  (let [userId (:userId request)]
    (read-user-response userId)))

(defn create-user-handler
  [request]
  (let [registration-data (:registration-data request)]
    (create-user-response registration-data)))

(defn user-routes  []
  ["/users" {}
   [""        {:get  {:handler list-user-handler}
                :post {:handler create-user-handler}}]
   ["/:userId" {:get  {:handler read-user-handler}}]])
