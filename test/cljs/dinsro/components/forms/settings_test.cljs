(ns dinsro.components.forms.settings-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.settings :as c.f.settings]
   [taoensso.timbre :as timbre]))

(cards/header "Settings Form Components" [])

(defcard-rg form
  (fn []
    [error-boundary
     [c.f.settings/form]]))
