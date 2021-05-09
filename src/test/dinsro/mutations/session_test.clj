(ns dinsro.mutations.session-test
  (:require
   [clojure.test :refer [use-fixtures]]
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.session :as mu.session]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.check :as _]
   [fulcro-spec.core :refer [assertions behavior specification]]
   [taoensso.timbre :as log]))

(def schemata
  [m.users/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(specification "do-register"
  (behavior "success"
    (let [password (ds/gen-key ::m.users/password)
          username (ds/gen-key ::m.users/id)
          response (mu.session/do-register username password)]
      (assertions
       (::m.users/id response) => username))))

(specification "register"
  (behavior "success"
    (try
      (let [env      {}
            data     #:user{:password "1234567" :username "bob"}
            f        (::pc/mutate mu.session/register)
            response (f env data)]

        (assertions
         (::m.users/id response) => (:user/username data)
         (::m.users/id response) =check=> (_/valid?* ::m.users/id)))
      (catch Exception ex
        (log/error ex "caught")))))

(specification "login"
  (let [env      {:request {:session {}}}
        data     #:user{:password "hunter2" :username "bob"}
        response ((::pc/mutate mu.session/login) env data)]
    (assertions
     response => #:user{:valid? false :username nil})))
