(ns dinsro.components.forms.settings-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.components.forms.settings :as c.f.settings]
   [taoensso.timbre :as timbre]))

(defcard-rg form
  [c.f.settings/form])
