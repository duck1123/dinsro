(ns dinsro.actions.nostr.subscription-pubkeys
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.actions.nostr.subscriptions :as a.n.subscriptions]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.subscription-pubkeys :as q.n.subscription-pubkeys]
   [dinsro.queries.nostr.subscriptions :as q.n.subscriptions]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/subscriptions.clj][Subscription Actions]]
;; [[../../queries/nostr/subscription_pubkeys.clj][Subscription Pubkey Queries]]
;; [[../../ui/nostr/subscription_pubkeys.cljs][Subscription Pubkeys UI]]

(>defn register-subscription!
  [subscription-id pubkey-id]
  [::m.n.subscriptions/id ::m.n.pubkeys/id => ::m.n.subscription-pubkeys/id]
  (log/info :register-subscription!/starting {:subscription-id subscription-id :pubkey-id pubkey-id})
  (if-let [subscription (q.n.subscriptions/read-record subscription-id)]
    (do
      (log/info :register-subscription!/exists {:subscription subscription})
      (if-let [sp-id (q.n.subscription-pubkeys/find-by-subscription-and-pubkey subscription-id pubkey-id)]
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

(defn do-subscribe!
  [props]
  (log/info :do-subscribe!/starting {:props props})
  (let [relay-id        (::m.n.relays/id props)
        pubkey-id       (::m.n.pubkeys/id props)
        subscription-id (a.n.subscriptions/register-subscription! relay-id "adhoc")]
    (log/info :do-subscribe!/subscription-registered {:subscription-id subscription-id})
    (let [sp-id (register-subscription! subscription-id pubkey-id)]
      (log/info :do-subscribe!/sp-registered {:sp-id sp-id})
      (let [item (q.n.subscription-pubkeys/read-record sp-id)]
        (log/info :do-subscribe!/parsed {:subscription-id subscription-id :sp-id sp-id})
        {:status                         "ok"
         ::m.n.subscription-pubkeys/item item}))))

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
          (throw (RuntimeException. "Failed to find subscription"))))
      updated-node)))

(defn do-fetch-contacts!
  [x]
  (log/info :do-fetch-contacts!/starting {:x x}))
