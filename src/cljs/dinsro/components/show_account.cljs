(ns dinsro.components.show-account
  (:require [devcards.core :refer-macros [defcard-rg]]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.links :as c.links]
            [dinsro.components.debug :as c.debug]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]
            [taoensso.timbre :as timbre]))

(defn show-account
  [account]
  (let [id (:db/id account)
        name (::s.accounts/name account)
        user-id (get-in account [::s.accounts/user :db/id])
        currency-id (get-in account [::s.accounts/currency :db/id])]
    [:<>
     [c.debug/debug-box account]
     [:h3 name]
     [:p
      (tr [:user-label])
      [c.links/user-link user-id]]
     [:p
      (tr [:currency-label])
      [c.links/currency-link currency-id]]
     (c.debug/hide [c.buttons/delete-account account])]))
