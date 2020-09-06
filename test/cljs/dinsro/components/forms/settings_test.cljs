(ns dinsro.components.forms.settings-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.settings :as c.f.settings]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.settings :as e.f.settings]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(cards/header "Settings Form Components" [])

(let [store (doto (mock-store)
              e.debug/init-handlers!
              e.f.settings/init-handlers!)]
  (defcard-rg form
    (fn []
      [error-boundary
       [c.f.settings/form store]]))

  (deftest form-test
    (is (vector? (c.f.settings/form store)))))
