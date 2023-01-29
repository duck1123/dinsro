(ns dinsro.actions.nostr.subscriptions
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.subscription-pubkeys :as q.n.subscription-pubkeys]
   [dinsro.queries.nostr.subscriptions :as q.n.subscriptions]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj][Relay Actions]]
;; [[../../model/nostr/subscriptions.cljc][Subscriptions Model]]
;; [[../../queries/nostr/subscriptions.clj][Subscription Queries]]
;; [[../../ui/nostr/subscriptions.cljs][Subscription UI]]

(>defn register-subscription!
  [relay-id code]
  [any? any? => any?]
  (log/info :register-subscription!/starting {:relay-id relay-id :code code})
  (let [params          {::m.n.subscriptions/code  code
                         ::m.n.subscriptions/relay relay-id}
        subscription-id (q.n.subscriptions/create-record params)]
    (log/info :register-subscription!/created {:subscription-id subscription-id})
    subscription-id))

(>defn fetch!
  [subscription-id]
  [::m.n.subscriptions/id => nil?]
  (log/info :fetch!/starting {:subscription-id subscription-id})
  (if-let [subscription (q.n.subscriptions/read-record subscription-id)]
    (do
      (log/info :fetch!/subscription-found {:subscription subscription})
      (let [relay-id (::m.n.subscriptions/relay subscription)]
        (if-let [relay (q.n.relays/read-record relay-id)]
          (do
            (log/info :fetch!/relay-found {:relay relay})
            nil)
          (throw (RuntimeException. "failed to find relay")))))
    (throw (RuntimeException. "failed to find subscription"))))

(comment
  (q.n.relays/delete-all)

  (def relay-id (first (q.n.relays/index-ids)))
  relay-id

  (def subscription-id (first (q.n.subscriptions/index-ids)))
  subscription-id

  (q.n.subscriptions/read-record subscription-id)
  (q.n.subscription-pubkeys/index-by-relay relay-id)

  nil)
