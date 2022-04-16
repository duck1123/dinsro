(ns dinsro.ui.core.nodes-test
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.client :as client]
   [dinsro.specs :as ds]
   [dinsro.ui.core.nodes :as u.c.nodes]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard ActionsMenu
  {::wsm/card-width 2 ::wsm/card-height 9}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.nodes/ActionsMenu
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn []
      {::m.c.nodes/id (ds/gen-key ::m.c.nodes/id)})}))

(defn ShowNode-data
  []
  {::m.c.nodes/id          (ds/gen-key ::m.c.nodes/id)
   ::m.c.nodes/chain       "regtest"
   ::m.c.nodes/block-count 73
   ::m.c.nodes/name        "main node"
   ::m.c.nodes/fetched?    true
   ::m.c.nodes/height      6
   ::m.c.nodes/hash        "yes"})

(defn NodePeersSubPage-data
  []
  (let [initial-report-data (comp/get-initial-state u.c.nodes/NodePeersSubPage)
        report-data (merge initial-report-data {:foo "bar"})]
    {::m.c.nodes/id (ds/gen-key ::m.c.nodes/id)
     :report        report-data}))

(ws/defcard NodePeersSubPage
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.c.nodes/NodePeersSubPage
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state NodePeersSubPage-data}))

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
