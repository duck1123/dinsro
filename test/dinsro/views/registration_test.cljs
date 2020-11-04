(ns dinsro.views.registration-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard defcard-rg deftest]]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.events.forms.settings :as e.f.settings]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.registration :as v.registration]
   [taoensso.timbre :as timbre]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.debug/init-handlers!
                e.f.registration/init-handlers!
                e.f.settings/init-handlers!)]
    store))

(let [match nil]

  (comment (defcard match match))

  (let [store (test-store)]
    (defcard-rg form-registration-not-enabled
      (st/dispatch store [::s.e.f.settings/set-allow-registration false])
      [v.registration/page store match])

    (deftest form-registration-not-enabled-test
      (is (vector? (v.registration/page store match)))))

  (let [store (test-store)]
    (defcard-rg form-registration-enabled
      (st/dispatch store [::s.e.f.settings/set-allow-registration true])
      [v.registration/page store match])

    (deftest form-registration-enabled-test
      (is (vector? (v.registration/page store match))))))
