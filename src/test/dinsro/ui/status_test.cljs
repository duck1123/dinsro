(ns dinsro.ui.status-test
  (:require
   [cljs.test :refer [is]]
   [day8.re-frame.http-fx]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.boundary]
   [dinsro.ui.status :as u.status]
   [taoensso.timbre :as timbre]))

(defn test-store
  []
  (let [store (doto (mock-store)
                u.status/init-handlers!)]
    store))

;; require-status-unloaded

(let [store (test-store)
      body [:p "Body passed to component"]]
  (defcard-rg require-status-unloaded
    [u.status/require-status store body])

  (deftest require-status-unloaded
    (is (vector? (u.status/require-status store body)))))

;; require-status-loaded

(let [store (test-store)
      body [:p "Body passed to component"]]
  (st/dispatch store [::u.status/set-status-state :loaded])

  (defcard-rg require-status-loaded
    [u.status/require-status store body])

  (deftest require-status-loaded
    (is (vector? (u.status/require-status store body)))))
