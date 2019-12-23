(ns dinsro.spec.events.forms.login
  (:require [clojure.spec.alpha :as s]
            [taoensso.timbre :as timbre]))

(s/def ::email string?)
(def email ::email)

(s/def ::password string?)
(def password ::password)
