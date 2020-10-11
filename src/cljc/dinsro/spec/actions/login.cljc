(ns dinsro.spec.actions.login
  (:require
   [clojure.spec.alpha :as s]
   [taoensso.timbre :as timbre]))

(s/def ::create-params-valid (s/keys))
