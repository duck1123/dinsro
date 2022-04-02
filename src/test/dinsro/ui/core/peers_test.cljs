(ns dinsro.ui.core.peers-test
  (:require
   [dinsro.specs :as ds]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.ui.core.peers :as u.c.peers]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(defn mock-peer
  []
  {::m.c.peers/id              (ds/gen-key ::m.c.peers/id)
   ::m.c.peers/addr            (ds/gen-key ::m.c.peers/addr)
   ::m.c.peers/connection-type (ds/gen-key ::m.c.peers/connection-type)
   ::m.c.peers/peer-id         (ds/gen-key ::m.c.peers/peer-id)})

(ws/defcard RefRow
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.peers/RefRow
    ::ct.fulcro3/initial-state
    (fn [] (mock-peer))}))

(ws/defcard RefTable
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.peers/RefTable
    ::ct.fulcro3/initial-state
    (fn []
      {:rows
       [(mock-peer)
        (mock-peer)
        (mock-peer)]})}))
