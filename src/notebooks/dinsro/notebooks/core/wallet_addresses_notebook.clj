^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.core.wallet-addresses-notebook
  (:require
   [dinsro.actions.nbxplorer :as a.nbxplorer]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Wallet Address Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def address "bcrt1q69zq0gn5cuflasuu8redktssdqxyxg8h6mh53j")

(def wallet-id (first (q.c.wallets/index-ids)))

(def wallet (q.c.wallets/read-record wallet-id))

wallet

(q.c.wallet-addresses/index-ids)

(q.c.wallet-addresses/find-by-wallet wallet-id)

(comment

  (tap> (q.c.wallets/index-ids))

  (q.c.wallet-addresses/index-records)

  (a.nbxplorer/track-address address)
  (a.nbxplorer/get-transactions-for-address address)

  nil)
