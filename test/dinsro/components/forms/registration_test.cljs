(ns dinsro.components.forms.registration-test
  (:require
   [dinsro.cards :refer-macros [defcard-rg]]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(let [store (doto (mock-store)
              e.debug/init-handlers!
              e.f.registration/init-handlers!)]
  (defcard-rg form
    [c.f.registration/form store]))
