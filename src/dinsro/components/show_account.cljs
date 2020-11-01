(ns dinsro.components.show-account
  (:require
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.links :as c.links]
   [dinsro.components.debug :as c.debug]
   [dinsro.specs.accounts :as s.accounts]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn show-account
  [store account]
  (let [name (::s.accounts/name account)
        user-id (get-in account [::s.accounts/user :db/id])
        currency-id (get-in account [::s.accounts/currency :db/id])]
    [:<>
     [:h3 name]
     [:p
      (tr [:user-label])
      [c.links/user-link store user-id]]
     [:p
      (tr [:currency-label])
      [c.links/currency-link store currency-id]]
     (c.debug/hide store [c.buttons/delete-account store account])]))
