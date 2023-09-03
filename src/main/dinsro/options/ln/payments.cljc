(ns dinsro.options.ln.payments
  (:require
   [dinsro.model.ln.payments :as m.ln.payments]))

(def id ::m.ln.payments/id)

(def payment-preimage ::m.ln.payments/payment-preimage)

(def payment-hash ::m.ln.payments/payment-hash)

(def payment-request ::m.ln.payments/payment-request)

(def status ::m.ln.payments/status)

(def fee ::m.ln.payments/fee)

(def value ::m.ln.payments/value)

(def payment-index ::m.ln.payments/payment-index)

(def failure-reason ::m.ln.payments/failure-reason)

(def creation-date ::m.ln.payments/creation-date)

(def node ::m.ln.payments/node)