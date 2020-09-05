(ns dinsro.components.forms.add-user-transaction-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.spec :as ds]
   [dinsro.translations :refer [tr]]))

(cards/header "Add User Transaction Form Components" [])

(defcard form-data
  (ds/gen-key ::e.f.add-user-transaction/form-data))

(defcard-rg form
  ;; "Create a transaction when the user is already provided"
  (fn []
    [error-boundary
     [c.f.add-user-transaction/form-shown]]))
