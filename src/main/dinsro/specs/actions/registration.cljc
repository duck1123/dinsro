(ns dinsro.specs.actions.registration
  (:require
   [clojure.spec.alpha :as s]
   [taoensso.timbre :as timbre]))

(s/def ::create-params-valid
  (s/keys))
