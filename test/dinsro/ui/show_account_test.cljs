(ns dinsro.ui.show-account-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.users :as e.users]
   [dinsro.specs.accounts :as s.accounts]
   [dinsro.specs.users :as s.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-account :as u.show-account]
   [taoensso.timbre :as timbre]))

(let [user-id 1
      user {:db/id user-id ::s.users/name "Bob"}
      account {::s.accounts/name "Bart"
               ::s.accounts/user {:db/id user-id}
               ::s.accounts/currency {:db/id 1}}
      store (doto (mock-store)
              e.debug/init-handlers!
              e.currencies/init-handlers!
              e.users/init-handlers!)]

  (defcard-rg show-account-card
    (st/dispatch store [::e.debug/set-shown? true])
    (st/dispatch store [::e.users/do-fetch-record-success {:item user}])

    [u.show-account/show-account store account])

  (deftest show-account-test
    (is (vector? (u.show-account/show-account store account)))))
