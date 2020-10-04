(ns dinsro.events-test
  (:require
   [dinsro.cards :as cards :include-macros true]
   [dinsro.events.accounts-test]
   [dinsro.events.admin-accounts-test]
   [dinsro.events.rates-test]
   [dinsro.events.transactions-test]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.events-test
 "Events" [])
