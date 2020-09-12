(ns dinsro.components.forms.create-account-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.create-account :as c.f.create-account]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.components.forms.create-account-test
 "Create Account Form Components" [])

(defn test-store
  []
  (let [store (doto (mock-store)
          e.currencies/init-handlers!
          e.debug/init-handlers!
          e.f.add-user-account/init-handlers!
          e.f.create-account/init-handlers!
          e.users/init-handlers!)]
    store))

(let [store (test-store)]

  (st/dispatch store [::e.f.create-account/set-shown? true])

  (defcard-rg form
    (fn []
      [error-boundary
       [c.f.create-account/form store]]))

  (deftest form-test
    (is (vector? (c.f.create-account/form store)))))
