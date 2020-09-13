(ns dinsro.components.forms.registration-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec :as ds]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.components.forms.registration-test
 "Registration Form Components" [])

(let [form-data (ds/gen-key ::e.f.registration/form-data)
      store (doto (mock-store)
              e.debug/init-handlers!
              e.f.registration/init-handlers!)]
  (defcard form-data-card form-data)

  (defcard-rg form
    (fn []
      [error-boundary
       [c.f.registration/form store]])))
