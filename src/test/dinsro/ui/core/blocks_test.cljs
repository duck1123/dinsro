(ns dinsro.ui.core.blocks-test
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.client :as client]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [lambdaisland.glogc :as log]))

(ws/defcard RefRow
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.blocks/RefRow
    ::ct.fulcro3/initial-state
    (fn [] {::m.c.blocks/id       :foo
            ::m.c.blocks/fetched? true
            ::m.c.blocks/height   6
            ::m.c.blocks/hash     "yes"})}))

(ws/defcard CoreBlockSubForm
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.blocks/CoreBlockSubForm
    ::ct.fulcro3/app
    {:client-will-mount
     (fn [app]
       (log/info :app/mounted {:app app})
       (client/setup-RAD app))}

    ::ct.fulcro3/initial-state
    (fn [] {::m.c.blocks/id       :foo
            ::m.c.blocks/fetched? true
            ::m.c.blocks/height   6
            ::m.c.blocks/hash     "yes"})}))

(ws/defcard CoreBlockReport
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.blocks/CoreBlockReport
    ::ct.fulcro3/initial-state
    (fn [] {::m.c.blocks/id       :foo
            ::m.c.blocks/fetched? true
            ::m.c.blocks/height   6
            ::m.c.blocks/hash     "yes"})}))
