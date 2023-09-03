(ns dinsro.options.ln.payreqs
  (:require
   [dinsro.model.ln.payreqs :as m.ln.payreqs]))
(def id ::m.ln.payreqs/id)
(def description ::m.ln.payreqs/description)
(def cltv-expiry ::m.ln.payreqs/cltv-expiry)
(def expiry ::m.ln.payreqs/expiry)
(def payment-hash ::m.ln.payreqs/payment-hash)
(def num-satoshis ::m.ln.payreqs/num-satoshis)
(def fallback-address ::m.ln.payreqs/fallback-address)
(def num-msats ::m.ln.payreqs/num-msats)
(def description-hash ::m.ln.payreqs/description-hash)
(def timestamp ::m.ln.payreqs/timestamp)

(def payment-request ::m.ln.payreqs/payment-request)

(def node ::m.ln.payreqs/node)
