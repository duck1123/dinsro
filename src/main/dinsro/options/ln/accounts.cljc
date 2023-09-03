(ns dinsro.options.ln.accounts
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.ln.accounts :as m.ln.accounts]))

(def id ::m.ln.accounts/id)

(def name ::m.ln.accounts/name)

(def user ::m.ln.accounts/user)

(def address-type ::m.ln.accounts/address-type)

(def master-key-fingerprint ::m.ln.accounts/master-key-fingerprint)

(def node ::m.ln.accounts/node)

(def wallet ::m.ln.accounts/wallet)
