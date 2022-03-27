(ns dinsro.actions.core.words
  (:require
   [dinsro.model.core.wallets :as m.wallets]
   [dinsro.queries.core.wallets :as q.wallets]
   [dinsro.queries.core.words :as q.words]))

(defn get-word-list
  [_wallet])

(comment
  (def wallet (first (q.wallets/index-records)))
  wallet
  (def wallet-id (::m.wallets/id wallet))

  (q.words/find-by-wallet wallet-id)

  nil)
