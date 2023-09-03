(ns dinsro.options.accounts
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.accounts :as m.accounts]))

;; [[../model/accounts.cljc]]
;; [[../mutations/accounts.cljc]]

(def currency
  "The currency that this account uses"
  ::m.accounts/currency)

(def id
  "The id of the account"
  ::m.accounts/id)

(def initial-value
  "The initial value when this account was created"
  ::m.accounts/initial-value)

(def name
  "The name of the account"
  ::m.accounts/name)

(def source
  "The rate source for this account

  deprecated: accounts shouldn't link to sources. Sources link to currencies."
  ::m.accounts/source)

(def user
  "The user this account belongs to"
  ::m.accounts/user)

(def wallet
  "This account's linked bitcoin wallet"
  ::m.accounts/wallet)
