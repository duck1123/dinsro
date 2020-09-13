(ns dinsro.spec-test
  (:require
   [dinsro.cards :as cards :include-macros true]
   [dinsro.spec.accounts-test]
   [dinsro.spec.currencies-test]
   [dinsro.spec.rates-test]
   [dinsro.spec.transactions-test]
   [dinsro.spec.users-test]
   [dinsro.spec.views-test]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.spec-test
 "Specs" [])
