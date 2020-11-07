(ns dinsro.events.forms.registration-test
  (:require
   [clojure.spec.alpha]
   [dinsro.cards :refer-macros [defcard-rg]]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec :as ds]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [taoensso.timbre :as timbre]))

(let [form-data (ds/gen-key ::e.f.registration/form-data)]
  (assert-spec ::e.f.registration/form-data form-data)

  (defcard-rg form
    [c.f.registration/form]))
