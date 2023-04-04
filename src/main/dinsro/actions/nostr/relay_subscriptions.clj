(ns dinsro.actions.nostr.relay-subscriptions
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.actions.nostr.subscriptions :as a.n.subscriptions]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.subscriptions :as q.n.subscriptions]
   [lambdaisland.glogc :as log]))

(>defn do-fetch!
  "Handler for fetch! mutation"
  [{relay-id ::m.n.relays/id}]
  [::m.n.relays/ident => ::m.n.relays/item]
  (log/info :do-fetch!/starting {:relay-id relay-id})
  (let [updated-node (q.n.relays/read-record relay-id)]
    (a.n.relays/connect! relay-id)
    (let [subscription-ids (q.n.subscriptions/index-ids)]
      (doseq [subscription-id subscription-ids]
        (log/info :do-fetch!/processing-subscription {:subscription-id subscription-id})
        (if-let [subscription (q.n.subscriptions/read-record subscription-id)]
          (do
            (log/info :do-fetch!/subscription-read {:subscription subscription})
            (a.n.subscriptions/fetch! subscription-id))
          (throw (ex-info "Failed to find subscription" {}))))
      updated-node)))
