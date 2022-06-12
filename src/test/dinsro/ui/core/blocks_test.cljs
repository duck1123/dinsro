(ns dinsro.ui.core.blocks-test
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.specs :as ds]
   [dinsro.client :as client]
   dinsro.machines
   [dinsro.ui.core.blocks :as u.c.blocks]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard RefRow
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.blocks/RefRow
    ::ct.fulcro3/initial-state
    (fn [] {::m.c.blocks/id       :foo
            ::m.c.blocks/fetched? true
            ::m.c.blocks/height   6
            ::m.c.blocks/hash     "yes"})}))

(ws/defcard CoreBlockSubForm
  {::wsm/card-width 4 ::wsm/card-height 9}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.blocks/CoreBlockSubForm
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] {::m.c.blocks/id       :foo
            ::m.c.blocks/fetched? true
            ::m.c.blocks/height   6
            ::m.c.blocks/hash     "yes"})}))

(defn CoreBlockReport-data
  []
  {:ui/busy?        false
   :ui/cache        {}
   :ui/controls     []
   :ui/current-page 1
   :ui/current-rows (map (fn [_] (ds/gen-key ::u.c.blocks/row)) (range 3))
   :ui/loaded-data  []
   :ui/page-count   1
   :ui/parameters   {}})

(ws/defcard CoreBlockReport
  {::wsm/card-width 6 ::wsm/card-height 13}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.c.blocks/CoreBlockReport
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state CoreBlockReport-data}))
