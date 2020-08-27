(ns dinsro.components.show-user-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.show-user :as c.show-user]
   [dinsro.spec.users :as s.users]
   [dinsro.translations :refer [tr]]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(cards/header "Show User Components" [])

(let [user {::s.users/name "Bart"
               ::s.users/user {:db/id 1}
            ::s.users/currency {:db/id 1}}
      store (mock-store)]
  (defcard user user)

  (defcard-rg show-user
    (fn []
      [error-boundary
       (c.show-user/show-user store user)])))
