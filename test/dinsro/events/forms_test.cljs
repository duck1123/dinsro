(ns dinsro.events.forms-test
  (:require
   [dinsro.cards :as cards :include-macros true]
   [dinsro.events.forms.add-account-transaction-test]
   [dinsro.events.forms.add-currency-rate-test]
   [dinsro.events.forms.add-user-account-test]
   [dinsro.events.forms.add-user-transaction-test]
   [dinsro.events.forms.create-account-test]
   [dinsro.events.forms.create-rate-test]
   [dinsro.events.forms.create-transaction-test]
   [dinsro.events.forms.registration-test]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.events.forms-test
 "Form Events" [])
