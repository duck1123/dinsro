(ns dinsro.ui.core.peers-test
  (:require
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.ui.core.peers :as u.c.peers]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(ws/defcard RefRow
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.peers/RefRow
    ::ct.fulcro3/initial-state
    (fn []
      {::m.c.peers/id   :foo
       ::m.c.peers/addr "foo"})}))

(ws/defcard RefTable
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.peers/RefTable
    ::ct.fulcro3/initial-state
    (fn []
      {:rows
       [{::m.c.peers/id   :foo
         ::m.c.peers/addr "foo"}]})}))
