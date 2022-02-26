(ns dinsro.actions.words
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.wallets :as m.wallets]
   [dinsro.queries.core-block :as q.core-block]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.wallets :as q.wallets]
   [dinsro.queries.words :as q.words]
   [lambdaisland.glogc :as log]))

(defn get-word-list
  [wallet])

(comment
  (def wallet (first (q.wallets/index-records)))
  wallet
  (def wallet-id (::m.wallets/id wallet))

  (q.words/find-by-wallet wallet-id)

  nil)
