(ns dinsro.components.forms.registration-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec :as ds]
   [taoensso.timbre :as timbre]))

(cards/header "Registration Form Components" [])

(let [form-data (ds/gen-key ::e.f.registration/form-data)]
  (defcard form-data-card form-data)

  (defcard-rg form
    (fn []
      [error-boundary
       (c.f.registration/form)])))
