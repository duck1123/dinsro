(ns dinsro.routes.home
  (:require [dinsro.layout :as layout]
            [dinsro.db.core :as db]
            [dinsro.middleware :as middleware]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn home-page [request]
  (layout/render "home.html"))

(defn docs-page [request]
  (-> (io/resource "docs/docs.md" slurp)
      response/ok
      (response/header "Content-Type" "text/plain; charset=utf-8")))

(defn home-routes []
  ["" {:middleware [
                    ;; middleware/wrap-csrf
                    middleware/wrap-formats]}
   #_["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "my-api"
                            :description "with reitit-ring"}}
           :handler (swagger/create-swagger-handler)}}]

   ["/" {:get home-page}]
   ["/docs" {:get docs-page}]])
