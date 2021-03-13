(ns dinsro.ui.forms.settings-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.settings :as e.f.settings]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.forms.settings :as u.f.settings]
   [taoensso.timbre :as timbre]))

(let [store (doto (mock-store)
              e.debug/init-handlers!
              e.f.settings/init-handlers!)]
  (defcard-rg form
    [u.f.settings/form store])

  (deftest form-test
    (is (vector? (u.f.settings/form store)))))
