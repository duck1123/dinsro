^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.tx-notebook
  (:require
   [dinsro.actions.core.tx :as a.c.tx]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.tx :as q.c.tx]
   [dinsro.queries.core.tx-in :as q.c.tx-in]
   [dinsro.queries.core.tx-out :as q.c.tx-out]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core TX Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def node-alice (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-alice")))
(def node-bob (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-bob")))

(def id (first (q.c.tx/index-ids)))
(def tx (q.c.tx/read-record id))
(def block-id (::m.c.tx/block tx))

(comment

  (tap> (q.c.nodes/index-records))

  (a.c.tx/update-tx (first (q.c.nodes/index-ids))
                    block-id
                    "8d3b5c3f7e726b57cdd293885f74c28773ee9682548756c7f393e76a2b935a20")

  (def node node-alice)
  (def node-id (::m.c.nodes/id node))
  (def tx-id "0ee607c65f65ccb74f79f4cd936dedb7779199aabaca417ac3ca63a7a23daed4")

  (q.c.nodes/index-ids)

  (q.c.blocks/read-record block-id)
  (q.c.nodes/find-by-tx id)

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

  (a.c.tx/update-tx node-id block-id tx-id)

  nil)
