(ns dinsro.actions.status-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            ;; [dinsro.actions.currencies :as a.currencies]
            [dinsro.actions.status :as a.status]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.mocks :as mocks]
            ;; [dinsro.model.currencies :as m.currencies]
            ;; [dinsro.spec.actions.currencies :as s.a.currencies]
            ;; [dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
            [mount.core :as mount]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre])
  )

(deftest status-handler-no-identity
  (let [request {}
        response (a.status/status-handler request)]
    (is (= nil (get-in response [:body :identity])))))
