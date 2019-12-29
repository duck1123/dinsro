(ns dinsro.components.show-account-test
  (:require [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.components.show-account :as c.show-account]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]
            [taoensso.timbre :as timbre]))

(let [account {::s.accounts/name "Bart"
               ::s.accounts/user {:db/id 1}
               ::s.accounts/currency {:db/id 1}}]
  (defcard account account)

  (defcard-rg show-account
    [c.show-account/show-account account])

  (defcard show-account2
    (reagent.core/as-element [c.show-account/show-account account])))
