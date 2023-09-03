(ns dinsro.options.contacts
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.contacts :as m.contacts]))

;; [[../model/contacts.cljc]]

(def id ::m.contacts/id)

(def name ::m.contacts/name)

(def pubkey ::m.contacts/pubkey)

(def user ::m.contacts/user)
