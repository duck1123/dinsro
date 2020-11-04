(ns dinsro.components.forms.registration-test
  (:require
   [dinsro.cards :refer-macros [defcard defcard-rg]]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec :as ds]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(let [form-data (ds/gen-key ::e.f.registration/form-data)
      store (doto (mock-store)
              e.debug/init-handlers!
              e.f.registration/init-handlers!)]
  (defcard form-data-card form-data)

  (defcard-rg form
    [c.f.registration/form store]))
