(ns dinsro.ui.user-accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [taoensso.timbre :as timbre]))

(let [user-id 1
      accounts []
      store (doto (mock-store)
              e.debug/init-handlers!
              e.currencies/init-handlers!
              e.f.add-user-account/init-handlers!)]

  (deftest index-accounts-test
    (is (vector? (u.user-accounts/index-accounts store accounts))))

  (defcard-rg index-accounts
    [u.user-accounts/index-accounts store accounts])

  (deftest section-test
    (is (vector? (u.user-accounts/section store user-id accounts))))


  (defcard-rg section
    [u.user-accounts/section store user-id accounts]))
