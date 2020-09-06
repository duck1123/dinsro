(ns dinsro.components.show-account-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.show-account :as c.show-account]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.users :as e.users]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.users :as s.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(cards/header "Show Account Components" [])

(let [user-id 1
      user {:db/id user-id ::s.users/name "Bob"}
      account {::s.accounts/name "Bart"
               ::s.accounts/user {:db/id user-id}
               ::s.accounts/currency {:db/id 1}}
      store (doto (mock-store)
              e.debug/init-handlers!
              e.currencies/init-handlers!
              e.users/init-handlers!)]

  (comment (defcard user user))
  (comment (defcard account account))

  (defcard-rg show-account-card
    (fn []
      (st/dispatch store [::e.debug/set-shown? true])
      (st/dispatch store [::e.users/do-fetch-record-success {:item user}])

      [error-boundary
       [c.show-account/show-account store account]]))

  (deftest show-account-test
    (is (vector? (c.show-account/show-account store account)))))
