(ns dinsro.specs.actions.settings
  (:require
   [clojure.spec.alpha :as s]
   [taoensso.timbre :as timbre]))

(s/def ::create-params-valid
  (s/keys
   ;; :req-un [
   ;;                 ::s.accounts/name
   ;;                 ::initial-value
   ;;                 ::s.accounts/user-id
   ;;                 ::s.accounts/currency-id
   ;;                 ]

          ))
