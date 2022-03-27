(ns dinsro.actions.core.words
  (:require
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.words :as q.c.words]))

(defn get-word-list
  [_wallet])

(comment
  (def wallet (first (q.c.wallets/index-records)))
  wallet
  (def wallet-id (::m.c.wallets/id wallet))

  (q.c.words/find-by-wallet wallet-id)

  nil)
