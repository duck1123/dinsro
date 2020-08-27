(ns dinsro.components.forms.settings-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.settings :as c.f.settings]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(cards/header "Settings Form Components" [])

(let [store (mock-store)]
  (defcard-rg form
    (fn []
      [error-boundary
       (c.f.settings/form store)])))
