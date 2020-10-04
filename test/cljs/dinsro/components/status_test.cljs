(ns dinsro.components.status-test
  (:require
   [cljs.test :refer [is]]
   [day8.re-frame.http-fx]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.status :as c.status]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.components.status-test
 "Status Components"
 [#{:status :components} #{:status}])

(defn test-store
  []
  (let [store (doto (mock-store)
                c.status/init-handlers!)]
    store))

;; require-status-unloaded

(let [store (test-store)
      body [:p "Body passed to component"]]
  (defcard-rg require-status-unloaded
    (fn []
      [error-boundary
       [c.status/require-status store body]]))

  (deftest require-status-unloaded
    (is (vector? (c.status/require-status store body)))))

;; require-status-loaded

(let [store (test-store)
      body [:p "Body passed to component"]]
  (st/dispatch store [::c.status/set-status-state :loaded])

  (defcard-rg require-status-loaded
    (fn []
      [error-boundary
       [c.status/require-status store body]]))

  (deftest require-status-loaded
    (is (vector? (c.status/require-status store body)))))
