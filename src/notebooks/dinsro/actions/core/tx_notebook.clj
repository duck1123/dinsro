^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.tx-notebook
  (:require
   [dinsro.actions.core.tx :as a.c.tx]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.tx :as q.c.tx]
   [dinsro.queries.core.tx-in :as q.c.tx-in]
   [dinsro.queries.core.tx-out :as q.c.tx-out]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core TX Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def transaction-id (first (q.c.tx/index-ids)))

^{::clerk/viewer clerk/code}
(def tx (q.c.tx/read-record transaction-id))

(def block-id (::m.c.tx/block tx))

^{::clerk/viewer clerk/table}
(when-let [ids (q.c.tx/index-ids)]
  ;; (sort-by ::m.c.tx/transaction
  (map q.c.tx/read-record ids)

           ;; )
  )

^{::clerk/viewer clerk/table}
(when-let [ids (q.c.tx-in/index-ids)]
  (sort-by ::m.c.tx-in/transaction (map q.c.tx-in/read-record ids)))

^{::clerk/viewer clerk/table}
(when-let [ids (q.c.tx-out/index-ids)]
  (map q.c.tx-out/read-record ids))

(q.c.tx/fetch-by-txid "929e51750265a3ee9ee317761d736d8f4249a5b9d3ee4bc46fe4e0c2cc921e2f")
(q.c.tx/fetch-by-txid "473ab1abdcf1986ea34ecda715c3339a53356e5a2e70255b3a44bf9f3644234a")

(q.c.tx-out/find-by-tx-id-and-index "929e51750265a3ee9ee317761d736d8f4249a5b9d3ee4bc46fe4e0c2cc921e2f" 0)

(comment

  (a.c.tx/update-tx (first (q.c.nodes/index-ids))
                    block-id
                    "8d3b5c3f7e726b57cdd293885f74c28773ee9682548756c7f393e76a2b935a20")

  (def tx-id "0ee607c65f65ccb74f79f4cd936dedb7779199aabaca417ac3ca63a7a23daed4")

  (q.c.nodes/index-ids)

  (q.c.blocks/read-record block-id)
  (q.c.nodes/find-by-tx transaction-id)

  (q.c.tx/index-ids)
  (def tx-id2 (::m.c.tx/tx-id (first (q.c.tx/index-records))))
  tx-id2

  (a.c.tx/search! {::m.c.tx/tx-id tx-id2})
  (a.c.tx/search! {::m.c.tx/tx-id "foo"})
  (tap> (a.c.tx/search! {::m.c.tx/tx-id tx-id2}))

  (q.c.tx-in/index-records)
  (q.c.tx-out/index-ids)

  (map q.c.tx-out/delete! (q.c.tx-out/index-ids))
  (map q.c.tx-in/delete! (q.c.tx-in/index-ids))
  (map q.c.tx/delete (q.c.tx/index-ids))
  (map q.c.blocks/delete (q.c.blocks/index-ids))

  (q.c.blocks/index-ids)

  (a.c.tx/update-tx n.lnd/node-alice-id block-id tx-id)

  nil)
