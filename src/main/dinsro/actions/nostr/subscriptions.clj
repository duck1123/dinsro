(ns dinsro.actions.nostr.subscriptions
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.queries.nostr.subscriptions :as q.n.subscriptions]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/subscriptions.cljc][Subscriptions Model]]
;; [[../../queries/nostr/subscriptions.clj][Subscription Queries]]

(>defn register-subscription!
  [relay-id code]
  [any? any? => any?]
  (log/info :register-subscription!/starting {:relay-id relay-id :code code})
  (let [params          {::m.n.subscriptions/code  code
                         ::m.n.subscriptions/relay relay-id}
        subscription-id (q.n.subscriptions/create-record params)]
    (log/info :register-subscription!/created {:subscription-id subscription-id})
    subscription-id))

(comment

  (def subscription-id (first (q.n.subscriptions/index-ids)))
  subscription-id

  (q.n.subscriptions/read-record subscription-id)

  nil)
