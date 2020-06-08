(ns dinsro.components.show-user-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.show-user :as c.show-user]
   [dinsro.spec.users :as s.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(let [user {::s.users/name "Bart"
               ::s.users/user {:db/id 1}
               ::s.users/currency {:db/id 1}}]
  (defcard user user)

  (defcard-rg show-user
    [error-boundary
     [c.show-user/show-user user]]))
