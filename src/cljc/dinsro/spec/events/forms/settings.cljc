(ns dinsro.spec.events.forms.settings
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::allow-registration boolean?)
(def allow-registration ::allow-registration)

(s/def ::first-run boolean?)
(def first-run ::first-run)
