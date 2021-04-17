(ns dinsro.mutations.session-test
  (:require
   [clojure.test :refer [use-fixtures]]
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.session :as mu.session]
   [dinsro.test-helpers :as th]
   [fulcro-spec.check :as _]
   [fulcro-spec.core :refer [assertions specification]]
   [taoensso.timbre :as timbre]))

(def schemata
  [m.users/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(specification "register"
  (try
    (let [env      {}
          data     #::m.users{:name "bob" :email "foo@bar.baz" :password "1234567"}
          f        (::pc/mutate mu.session/register)
          response (f env data)]

      (assertions
       (::m.users/email response) => (::m.users/email data)
       (::m.users/id response) =check=> (_/valid?* ::m.users/id)))
    (catch Exception ex
      (timbre/error ex "caught"))))

(specification "login"
  (let [env      {:request {:session {}}}
        data     #::m.users{:email "foo@bar.baz" :name "bob" :password "hunter2"}
        response ((::pc/mutate mu.session/login) env data)]
    (assertions
     response => #:user{:valid? false :id nil})))
