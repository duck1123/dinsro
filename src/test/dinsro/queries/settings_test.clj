(ns dinsro.queries.settings-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.queries.settings :as q.settings]
   [dinsro.test-helpers :as th]
   [taoensso.timbre :as timbre]))

(def schemata
  [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest get-settings
  (let [results (q.settings/get-settings)]
    (is (contains? results :allow-registration))
    (is (contains? results :first-run))))
