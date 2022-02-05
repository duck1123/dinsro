(ns dinsro.actions.authentication-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest do-register-success
  (let [password (ds/gen-key ::m.users/password)
        username (ds/gen-key ::m.users/name)
        response (a.authentication/do-register username password)]
    (assertions
     (::m.users/name response) => username)))
