(ns dinsro.views.settings-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.settings :as e.f.settings]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.settings :as v.settings]
   [taoensso.timbre :as timbre]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.debug/init-handlers!
                e.f.settings/init-handlers!)]
    store))

(let [match nil
      store (test-store)]
  (defcard-rg page-card
    [v.settings/page store match])

  (deftest page-test
    (is (vector? (v.settings/page store match)))))
