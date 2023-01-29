(ns dinsro.actions.nostr.subscriptions
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [lambdaisland.glogc :as log]))

(>defn register-subscription!
  [relay-id code]
  [any? any? => any?]
  (log/info :register-subscription!/starting {:relay-id relay-id :code code}))
