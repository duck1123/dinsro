^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.model.core.blocks-notebook
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Blocks Model

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
