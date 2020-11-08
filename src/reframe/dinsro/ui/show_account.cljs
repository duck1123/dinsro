(ns dinsro.ui.show-account
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.debug :as u.debug]
   [taoensso.timbre :as timbre]))

(defn show-account
  [store account]
  (let [name (::m.accounts/name account)
        user-id (get-in account [::m.accounts/user :db/id])
        currency-id (get-in account [::m.accounts/currency :db/id])]
    [:<>
     [:h3 name]
     [:p
      (tr [:user-label])
      [u.links/user-link store user-id]]
     [:p
      (tr [:currency-label])
      [u.links/currency-link store currency-id]]
     (u.debug/hide store [u.buttons/delete-account store account])]))
