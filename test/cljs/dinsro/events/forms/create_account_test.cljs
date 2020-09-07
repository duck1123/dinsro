(ns dinsro.events.forms.create-account-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.create-account :as c.f.create-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.store.mock :refer [mock-store]]))

(cards/header "Create Account Form Events" [])

(let [store (doto (mock-store)
              e.f.create-account/init-handlers!)]

  (defcard-rg form
    (fn []
      [error-boundary
       [c.f.create-account/form store]])))
