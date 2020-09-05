(ns dinsro.views.login-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [deftest]]
   [dinsro.cards :as cards]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.login :as v.login]
   [taoensso.timbre :as timbre]))

(cards/header "Login View" [])

(deftest page
  (let [store (mock-store)
        match nil]
    (is (vector? (v.login/page store match)))))
