(ns dinsro.routes.services-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [dinsro.db.core :as db]
            [dinsro.handler :refer :all]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]
            ))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'dinsro.config/env
                 #'dinsro.handler/app)
    (f)))

(deftest swagger-test
  (let [response (app (request :get "/swagger.json"))]
    (is (= 200 (:status response)))))
