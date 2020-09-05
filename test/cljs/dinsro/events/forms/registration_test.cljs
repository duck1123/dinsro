(ns dinsro.events.forms.registration-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec :as ds]
   [taoensso.timbre :as timbre]))

(cards/header "Registration Form Events" [])

(let [form-data (ds/gen-key ::e.f.registration/form-data)]

  (defcard form-data-card form-data)

  (defcard-rg form
    [c.f.registration/form]))
