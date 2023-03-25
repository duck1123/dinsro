^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.components.seed-notebook
  (:require
   [dinsro.components.seed :as c.seed]
   [dinsro.components.seed.accounts :as cs.accounts]
   [dinsro.components.seed.categories :as cs.categories]
   [dinsro.components.seed.core :as cs.core]
   [dinsro.components.seed.currencies :as cs.currencies]
   [dinsro.components.seed.ln-nodes :as cs.ln-nodes]
   [dinsro.components.seed.rate-sources :as cs.rate-sources]
   [dinsro.components.seed.rates :as cs.rates]
   [dinsro.components.seed.transactions :as cs.transactions]
   [dinsro.components.seed.wallets :as cs.wallets]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [expound.alpha :as expound]
   [nextjournal.clerk :as clerk]))

;; # Seed Component

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; ## Addresses

^{::clerk/viewer clerk/code}
(ds/gen-key ::cs.core/addresses)

;; ## Currencies

^{::clerk/viewer clerk/code}
(ds/gen-key ::cs.currencies/item)

;; ## Rate Sources

^{::clerk/viewer clerk/code}
(ds/gen-key ::cs.rate-sources/item)

;; ## Rate

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::cs.rates/item)

;; ## Category Names

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::cs.core/category-names)

;; ## Networks

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::cs.core/networks)

;; ## Nodes

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::cs.core/nodes)

;; ## Users

;; ^{::clerk/viewer clerk/code ::clerk/no-cache true}
;; (ds/gen-key ::cs.core/users)

;; ## Accounts

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::cs.accounts/item)

;; ## Categories

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::cs.categories/item)

;; ## Transactions

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::cs.transactions/item)

;; ## LN Nodes

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::cs.ln-nodes/item)

;; ## Wallets

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::cs.wallets/item)

;; ^{::clerk/viewer clerk/code}
;; (ds/gen-key ::c.seed/default-rate-sources)

;; ## users

;; ^{::clerk/viewer clerk/code}
;; (ds/gen-key ::c.seed/users)

;; ## timezone

(ds/gen-key ::cs.core/timezone)

;; ## seed-data

;; ^{::clerk/viewer clerk/code}
;; (ds/gen-key ::cs.core/seed-data)

^{::clerk/no-cache true}
(expound/expound-str
 :dinsro.components.seed.core/seed-data
 (c.seed/get-seed-data))

(comment

  (c.seed/get-seed-data)

  nil)

21
