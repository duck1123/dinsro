(ns dinsro.specs.actions.settings
  (:require
   [clojure.spec.alpha :as s]
   [taoensso.timbre :as timbre]))

(s/def ::create-params-valid
  (s/keys
   ;; :req-un [
   ;;                 ::m.accounts/name
   ;;                 ::initial-value
   ;;                 ::m.accounts/user-id
   ;;                 ::m.accounts/currency-id
   ;;                 ]
   ))
