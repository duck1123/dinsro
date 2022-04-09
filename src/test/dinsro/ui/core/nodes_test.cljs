(ns dinsro.ui.core.nodes-test
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.client :as client]
   dinsro.machines
   [dinsro.ui.core.nodes :as u.c.nodes]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(comment ::ct.fulcro3/_ ::m.c.nodes/_ ::m.c.blocks/_ ::wsm/_)

(ws/defcard ShowNode
  {::wsm/card-width 7 ::wsm/card-height 14}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.nodes/ShowNode
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
