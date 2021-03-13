(ns dinsro.events.forms.registration-test
  (:require
   [clojure.spec.alpha]
   [dinsro.cards :refer-macros [assert-spec defcard-rg]]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.specs :as ds]
   [dinsro.ui.forms.registration :as u.f.registration]
   [taoensso.timbre :as timbre]))

(let [form-data (ds/gen-key ::e.f.registration/form-data)]
  (assert-spec ::e.f.registration/form-data form-data)

  (defcard-rg form
    [u.f.registration/form]))
