(ns dinsro.options.users
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.users :as m.users]))

(def id ::m.users/id)

(def name ::m.users/name)

(def password ::m.users/password)

(def hashed-value ::m.users/hashed-value)

(def salt ::m.users/salt)

(def role ::m.users/role)
