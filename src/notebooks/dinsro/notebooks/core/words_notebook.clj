^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.core.words-notebook
  (:require
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.words :as q.c.words]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Word Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def wallet (first (q.c.wallets/index-records)))
wallet
(def wallet-id (::m.c.wallets/id wallet))

(q.c.words/find-by-wallet wallet-id)

(comment

  nil)
