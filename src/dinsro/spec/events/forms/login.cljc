(ns dinsro.spec.events.forms.login
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::email string?)
(def email ::email)

(s/def ::password string?)
(def password ::password)
