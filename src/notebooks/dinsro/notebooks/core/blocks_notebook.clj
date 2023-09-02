^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.core.blocks-notebook
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Blocks

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; ## params

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.c.blocks/params)

;; ## item

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.c.blocks/item)

^{::clerk/viewer clerk/table}
m.c.blocks/attributes

;; ## prepare params

(def unprepared-params
  {:hash                 "0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206",
   :difficulty           0,
   :time                 1296688602,
   :stripped-size        285,
   :previous-block-hash  nil,
   ::m.c.blocks/fetched? true,
   :bits                 545259519,
   :median-time          1296688602,
   :size                 285,
   :confirmations        1,
   :tx                   ["4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"]
   :weight               1140,
   :next-block-hash      nil,
   :chainwork            "0000000000000000000000000000000000000000000000000000000000000002",
   :version-hex          1,
   :version              1,
   :nonce                2,
   :merkle-root          "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b",
   :height               0})

^{::clerk/viewer clerk/code}
(def prepared-params
  (try
    (m.c.blocks/prepare-params unprepared-params)
    (catch Exception ex ex)))

(comment

  (let [unprepared-params {}]
    (m.c.blocks/prepare-params unprepared-params))

  nil)

^{::clerk/viewer clerk/code}
(def node-alice (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-alice")))
(def node-bob (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-bob")))
(def node node-alice)
(def alice-id (::m.c.nodes/id node-alice))

(def alice-block-ids (q.c.blocks/index-ids {::m.c.nodes/id alice-id}))

(def all-ids (q.c.blocks/index-ids))

^{::clerk/viewer clerk/code}
(q.c.networks/read-record (q.c.networks/find-by-core-node alice-id))

(ds/gen-key ::m.c.blocks/item)

;; ## nodes

;; This is all the nodes currently defined

^{::clerk/viewer clerk/table}
(map q.c.nodes/read-record (q.c.nodes/index-ids))

;; ## blocks

^{::clerk/viewer clerk/table}
(map q.c.blocks/read-record all-ids)

^{::clerk/viewer clerk/table}
(map q.c.blocks/read-record alice-block-ids)

(comment

  (q.c.blocks/find-by-node (::m.c.nodes/id node-bob))

  (q.c.blocks/fetch-by-node-and-height (::m.c.nodes/id node-alice) 97)

  nil)
