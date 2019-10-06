(ns dinsro.routes.home
  (:require [dinsro.layout :as layout]
            [dinsro.db.core :as db]
            [dinsro.middleware :as middleware]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn home-page [request]
  (layout/render "home.html"))

(defn docs-page [request]
  (-> (io/resource "docs/docs.md")
      slurp
      ;; TODO: Convert markdown
      response/ok
      (response/header "Content-Type" "text/plain; charset=utf-8")))

(defn home-routes []
  ["" {:middleware [
                    ;; middleware/wrap-csrf
                    middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get docs-page}]])
