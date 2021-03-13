(ns dinsro.ui.forms.registration-test
  (:require
   [dinsro.cards :refer-macros [defcard-rg]]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.forms.registration :as u.f.registration]
   [taoensso.timbre :as timbre]))

(let [store (doto (mock-store)
              e.debug/init-handlers!
              e.f.registration/init-handlers!)]
  (defcard-rg registration-form
    [u.f.registration/form store]))
