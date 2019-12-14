(ns dinsro.model.transactions-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.rates :as m.rates]
            [dinsro.model.transactions :as m.transactions]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.spec.transactions :as s.transactions]
            [mount.core :as mount]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (datahike.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.currencies/schema)
      (d/transact db/*conn* s.rates/schema)
      (d/transact db/*conn* s.transactions/schema)
      (f))))

(deftest create-record-test
  (let [params (gen/generate (s/gen ::s.transactions/params))
        id (m.transactions/create-record params)
        record (m.transactions/read-record id)]

    (is (= (double (::s.transactions/value params))
           (::s.transactions/value record))
        "values match")))
