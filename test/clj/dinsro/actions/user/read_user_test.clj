(ns dinsro.actions.user.read-user-test
  (:require [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [dinsro.actions.user.read-user :refer [read-user-response]]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.handler :as handler]
            [dinsro.model.user :as model.user]
            [dinsro.specs :as ds]
            [mount.core :as mount]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(deftest read-users
  (testing "when found"
    (let [params (gen/generate (s/gen ::ds/register-request))
          {:keys [id] :as user} (model.user/create-user! params)
          request {:path-params {:userId id}}
          response (read-user-response request)]
      (is (= status/ok (:status response)))
      (are [key] (= (get user key) (get-in response [:body key]))
        :id :email)))
  (testing "when not found"
    (db/delete-users!)
    (let [id (gen/generate (s/gen ::ds/user-id))
          request {:path-params {:userId id}}
          response (read-user-response request)]
      (is (= (:status response) status/not-found) "Should return a not-found response"))))
