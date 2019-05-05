(ns dinsro.routes.home
  (:require [dinsro.layout :as layout]
            [dinsro.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" []
    (home-page))
  (GET "/docs" []
    (-> (io/resource "docs/docs.md" slurp)
        response/ok
        (response/header "Content-Type" "text/plain; charset=utf-8"))))
