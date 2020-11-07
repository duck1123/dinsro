(ns dinsro.components.status-test
  (:require
   [cljs.test :refer [is]]
   [day8.re-frame.http-fx]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.boundary]
   [dinsro.components.status :as c.status]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(defn test-store
  []
  (let [store (doto (mock-store)
                c.status/init-handlers!)]
    store))

;; require-status-unloaded

(let [store (test-store)
      body [:p "Body passed to component"]]
  (defcard-rg require-status-unloaded
    [c.status/require-status store body])

  (deftest require-status-unloaded
    (is (vector? (c.status/require-status store body)))))

;; require-status-loaded

(let [store (test-store)
      body [:p "Body passed to component"]]
  (st/dispatch store [::c.status/set-status-state :loaded])

  (defcard-rg require-status-loaded
    [c.status/require-status store body])

  (deftest require-status-loaded
    (is (vector? (c.status/require-status store body)))))
