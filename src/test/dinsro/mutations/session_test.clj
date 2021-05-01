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
   [taoensso.timbre :as timbre]))

(def schemata
  [m.users/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(specification "do-register"
  (behavior "success"
    (let [password (ds/gen-key ::m.users/password)
          username (ds/gen-key ::m.users/username)
          response (mu.session/do-register username password)]
      (assertions
       (::m.users/username response) => username))))

(specification "register"
  (behavior "success"
    (try
      (let [env      {}
            data     #:user{:password "1234567" :username "bob"}
            f        (::pc/mutate mu.session/register)
            response (f env data)]

        (assertions
         (::m.users/username response) => (:user/username data)
         (::m.users/username response) =check=> (_/valid?* ::m.users/username)))
      (catch Exception ex
        (timbre/error ex "caught")))))

(specification "login"
  (let [env      {:request {:session {}}}
        data     #:user{:password "hunter2" :username "bob"}
        response ((::pc/mutate mu.session/login) env data)]
    (assertions
     response => #:user{:valid? false :username nil})))
