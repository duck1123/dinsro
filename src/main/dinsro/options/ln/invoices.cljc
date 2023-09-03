(ns dinsro.options.ln.invoices
  (:require
   [dinsro.model.ln.invoices :as m.ln.invoices]))

(def id ::m.ln.invoices/id)

(def amount-paid ::m.ln.invoices/amount-paid)

(def add-index ::m.ln.invoices/add-index)

(def cltv-expiry ::m.ln.invoices/cltv-expiry)

(def expiry ::m.ln.invoices/expiry)

(def private? ::m.ln.invoices/private?)

(def keysend? ::m.ln.invoices/keysend?)

(def value ::m.ln.invoices/value)

(def r-hash ::m.ln.invoices/r-hash)

(def r-preimage ::m.ln.invoices/r-preimage)

(def settled? ::m.ln.invoices/settled?)

(def payment-address ::m.ln.invoices/payment-address)

(def payment-request ::m.ln.invoices/payment-request)

(def payment-secret ::m.ln.invoices/payment-secret)

(def description ::m.ln.invoices/description)

(def node ::m.ln.invoices/node)