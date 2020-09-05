(ns dinsro.views.settings-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.settings :as v.settings]
   [taoensso.timbre :as timbre]))

(cards/header "Settings View" [])

(let [store (mock-store)
      match nil]
  (deftest page
    (is (vector? (v.settings/page store match)))))

(let [store (mock-store)
      match nil]
  (defcard-rg page-card
    (fn []
      [error-boundary
       (v.settings/page store match)])))
