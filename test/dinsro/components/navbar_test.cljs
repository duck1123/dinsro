(ns dinsro.components.navbar-test
  (:require
   [cljs.test :refer [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.navbar :as c.navbar]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.settings :as e.f.settings]
   [dinsro.events.navbar :as e.navbar]
   [dinsro.events.users :as e.users]
   [dinsro.mappings :as mappings]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def user (ds/gen-key ::e.users/item))
(def user-id (:db/id user))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.authentication/init-handlers!
                e.debug/init-handlers!
                e.f.settings/init-handlers!
                e.navbar/init-handlers!
                e.users/init-handlers!
                mappings/init-handlers!)]
    store))

;; nav-burger

(let [store (test-store)]
  (defcard-rg nav-burger
    [c.navbar/nav-burger store])
  (deftest nav-burger-test
    (is (vector? [c.navbar/nav-burger store]))))

;; navbar

(let [store (test-store)]
  (defcard-rg navbar
    [c.navbar/navbar store])
  (deftest navbar-test
    (is (vector? [c.navbar/navbar store]))))

;; navbar-expanded

(let [store (test-store)]
  (st/dispatch store [::e.navbar/set-expanded? true])

  (defcard-rg navbar-expanded
    [c.navbar/navbar store])
  (deftest navbar-expanded-test
    (is (vector? [c.navbar/navbar store]))))

;; navbar-authenticated

(let [store (test-store)]
  (st/dispatch store [::e.authentication/set-auth-id user-id])
  (st/dispatch store [::e.users/do-fetch-record-success {:user user}])

  (defcard-rg navbar-authenticated
    [c.navbar/navbar store])
  (deftest navbar-authenticated-test
    (is (vector? [c.navbar/navbar store]))))

;; navbar-authenticated-expanded

(let [store (test-store)]
  (st/dispatch store [::e.authentication/set-auth-id user-id])
  (st/dispatch store [::e.navbar/set-expanded? true])
  (st/dispatch store [::e.users/do-fetch-record-success {:user user}])

  (defcard-rg navbar-authenticated-expanded
    [c.navbar/navbar store])
  (deftest navbar-authenticated-expanded-test
    (is (vector? [c.navbar/navbar store]))))
