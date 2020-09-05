(ns dinsro.views.about-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [deftest]]
   [dinsro.cards :as cards]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.about :as about]
   [pjstadig.humane-test-output]))

(cards/header "About View" [])

(deftest about-page-test
  (let [store (mock-store)
        match nil]
    (is (vector? (about/page store match)))))
