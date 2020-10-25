(ns dinsro.views.login-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.login :as e.f.login]
   [dinsro.spec.events.forms.login :as s.e.f.login]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.login :as v.login]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.views.login-test
 "Login View" [])

(defn login-store
  []
  (doto (mock-store)
    e.authentication/init-handlers!
    e.debug/init-handlers!
    e.f.login/init-handlers!))

(let [email "bob@example.com"
      password "hunter2"
      store (login-store)
      match {:query-string "return-to=/"}]

  (st/dispatch store [::e.debug/set-shown? true])
  (st/dispatch store [::s.e.f.login/set-email email])
  (st/dispatch store [::s.e.f.login/set-password password])

  (defcard-rg page-card
    (fn []
      [error-boundary
       [v.login/page store match]]))

  (deftest page-test
    (is (vector? (v.login/page store match)))))