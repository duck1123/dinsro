^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.wallet-addresses-notebook
  (:require
   [dinsro.actions.core.wallet-addresses :as a.c.wallet-addresses]
   [dinsro.actions.nbxplorer :as a.nbxplorer]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Wallet Address Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def address "bcrt1q69zq0gn5cuflasuu8redktssdqxyxg8h6mh53j")

(def wallet-id (first (q.c.wallets/index-ids)))

(def wallet (q.c.wallets/read-record wallet-id))

(q.c.wallet-addresses/index-ids)

(q.c.wallet-addresses/find-by-wallet wallet-id)

(comment

  (a.c.wallet-addresses/register-addresses! wallet 20)

  (tap> (q.c.wallets/index-ids))

  (q.c.wallet-addresses/index-records)

  (a.nbxplorer/track-address address)
  (a.nbxplorer/get-transactions-for-address address)

  nil)
