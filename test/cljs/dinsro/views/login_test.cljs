(ns dinsro.views.login-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.login :as e.f.login]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.login :as v.login]
   [taoensso.timbre :as timbre]))

(cards/header "Login View" [])

(let [store (doto (mock-store)
              e.authentication/init-handlers!
              e.debug/init-handlers!
              e.f.login/init-handlers!)
      match nil]

  (defcard-rg page
    (fn []
      [error-boundary
       [v.login/page store match]]))

  (deftest page-test
    (is (vector? (v.login/page store match)))))
