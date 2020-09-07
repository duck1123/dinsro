(ns dinsro.components.user-accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.user-accounts :as c.user-accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(cards/header "User Accounts Components" [])

(let [user-id 1
      accounts []
      store (doto (mock-store)
              e.debug/init-handlers!
              e.currencies/init-handlers!
              e.f.add-user-account/init-handlers!)]

  (deftest index-accounts-test
    (is (vector? (c.user-accounts/index-accounts store accounts))))

  (defcard-rg index-accounts
    (fn []
      [error-boundary
       [c.user-accounts/index-accounts store accounts]]))

  (deftest section-test
    (is (vector? (c.user-accounts/section store user-id accounts))))


  (defcard-rg section
    (fn []
      [error-boundary
       [c.user-accounts/section store user-id accounts]])))
