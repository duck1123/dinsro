(ns dinsro.components.forms.add-user-transaction-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.spec :as ds]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]))

(cards/header "Add User Transaction Form Components" [])

(let [form-data (ds/gen-key ::e.f.add-user-transaction/form-data)
      store (mock-store)]

  (defcard form-data form-data)

  (defcard-rg form
    (fn []
      [error-boundary
       (c.f.add-user-transaction/form-shown store form-data)])))
