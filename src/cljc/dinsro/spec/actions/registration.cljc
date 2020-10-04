(ns dinsro.spec.actions.accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.accounts :as s.accounts]
   [taoensso.timbre :as timbre]))

(s/def ::create-params-valid
  (s/keys ))
