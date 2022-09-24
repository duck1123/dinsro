(ns dinsro.ui.core.nodes-test
  (:require
   [dinsro.client :as client]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.specs :as ds]
   [dinsro.ui.core.node-peers :as u.c.node-peers]
   [dinsro.ui.core.node-peers-test :as u.c.node-peers-test]
   [dinsro.ui.core.nodes :as u.c.nodes]
   [lambdaisland.glogc :as log]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn ShowNode-data
  []
  (let [data {::m.c.nodes/id          (ds/gen-key ::m.c.nodes/id)
              ::m.c.nodes/chain       "regtest"
              ::m.c.nodes/block-count 73
              ::m.c.nodes/name        "main node"
              ::m.c.nodes/fetched?    true
              ::m.c.nodes/height      6
              ::m.c.nodes/hash        "yes"
              :peers                  (u.c.node-peers-test/SubPage-data)}]
    (log/info :ShowNode-data/response {:data data})
    data))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard ActionsMenu
  {::wsm/card-width 2 ::wsm/card-height 9}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.nodes/ActionsMenu
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn []
      {::m.c.nodes/id (ds/gen-key ::m.c.nodes/id)})}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard SubPage
  {::wsm/card-width 7 ::wsm/card-height 20}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.c.node-peers/SubPage
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state u.c.node-peers-test/SubPage-data}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard ShowNode
  {::wsm/card-width 7 ::wsm/card-height 14}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.c.nodes/ShowNode
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state ShowNode-data}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreNodeForm
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.nodes/CoreNodeForm
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn []
      {::m.c.nodes/id          :foo
       ::m.c.nodes/chain       "regtest"
       ::m.c.nodes/block-count 73
       ::m.c.nodes/name        "main node"
       ::m.c.nodes/fetched?    true
       ::m.c.nodes/height      6
       ::m.c.nodes/hash        "yes"
       ::m.c.nodes/blocks
       [{::m.c.blocks/id       1
         ::m.c.blocks/fetched? true
         ::m.c.blocks/hash     "foo"
         ::m.c.blocks/height   69}]})}))
