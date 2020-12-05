(ns dinsro.ui.show-user-test
  (:require
   [cljs.test :refer [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.users :as e.users]
   [dinsro.model.users :as m.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.boundary]
   [dinsro.ui.show-user :as u.show-user]
   [taoensso.timbre :as timbre]))

(let [user {::m.users/name "Bart"
            ::m.users/email "bob@example.com"
            ::m.users/user {:db/id 1}
            ::m.users/currency {:db/id 1}}
      store (doto (mock-store)
              e.debug/init-handlers!
              e.users/init-handlers!)]
  (comment (st/dispatch store [::e.debug/set-shown? true]))

  (defcard-rg show-user
    [u.show-user/show-user store user])

  (deftest show-user-test
    (is (vector? (u.show-user/show-user store user)))))
