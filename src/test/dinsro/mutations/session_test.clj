(ns dinsro.mutations.session-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [com.fulcrologic.rad.authorization :as auth]
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.session :as mu.session]
   [dinsro.test-helpers :as th]
   [fulcro-spec.check :as _]
   [fulcro-spec.core :refer [assertions]]
   [lambdaisland.glogc :as log]))

(def schemata
  [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest register-success
  (try
    (let [env      {}
          data     #:user{:password "1234567" :username "bob"}
          f        (::pc/mutate mu.session/register)
          response (f env data)]
      (assertions
       (::m.users/name response) => (:user/username data)
       (::m.users/id response) =check=> (_/valid?* ::m.users/id)))
    (catch Exception ex
      (log/error :register-success/failed {:ex ex}))))

(deftest login
  (let [env      {:request {:session {}}}
        data     #:user{:password m.users/default-password :username "bob"}
        response ((::pc/mutate mu.session/login) env data)]
    (assertions
     response => {::auth/provider :local
                  ::auth/status   :failed})))
