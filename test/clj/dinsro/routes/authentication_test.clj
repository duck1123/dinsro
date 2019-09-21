(ns dinsro.routes.authentication-test
  (:require [clojure.test :refer :all]
            [dinsro.config :as config]
            [dinsro.handler :as handler]
            [mount.core :as mount]
            [ring.mock.request :as mock]))

(def url-root "/api/v1")

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'handler/app-routes)
    (f)))

(deftest authenticate-test
  (testing "successful"
    (let [path (str url-root "/authenticate")
          request (mock/request :post path)
          response ((handler/app) request)]
      (is (= 200 (:status response))))))
