(ns dinsro.actions.nostr.subscription-pubkeys
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.queries.nostr.subscription-pubkeys :as q.n.subscription-pubkeys]
   [dinsro.queries.nostr.subscriptions :as q.n.subscriptions]
   [lambdaisland.glogc :as log]))

(>defn register-subscription!
  [subscription-id pubkey-id]
  [::m.n.subscriptions/id ::m.n.pubkeys/id => ::m.n.subscription-pubkeys/id]
  (log/info :registration-subscription!/starting
            {:subscription-id subscription-id
             :pubkey-id       pubkey-id})
  (if-let [subscription (q.n.subscriptions/read-record subscription-id)]
    (do
      (log/info :register-subscription!/exists {:subscription subscription})
      (if-let [sp-id (q.n.subscription-pubkeys/find-by-subscription-and-pubkey
                      subscription-id pubkey-id)]
        (do
          (log/info :register-subscription!/sp-exists {:sp-id sp-id})
          sp-id)
        (do
          (log/info :register-subscription!/sp-not-exists {})
          (let [params {::m.n.subscription-pubkeys/pubkey       pubkey-id
                        ::m.n.subscription-pubkeys/subscription subscription-id}]
            (if-let [sp-id (q.n.subscription-pubkeys/create-record params)]
              sp-id
              (throw (RuntimeException. "failed")))))))
    (do
      (log/info :register-subscription!/not-exists {})
      (throw (RuntimeException. "Not exists")))))
