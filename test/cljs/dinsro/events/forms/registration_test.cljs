(ns dinsro.events.forms.registration-test
  (:require
   [clojure.spec.alpha]
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec :as ds]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.events.forms.registration-test
 "Registration Form Events" [])

(let [form-data (ds/gen-key ::e.f.registration/form-data)]

  (defcard form-data-card form-data)

  (assert-spec ::e.f.registration/form-data form-data)

  (defcard-rg form
    [c.f.registration/form]))
