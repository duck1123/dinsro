(ns dinsro.ui.core.blocks-test
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.specs :as ds]
   [dinsro.client :as client]
   dinsro.machines
   [dinsro.ui.core.blocks :as u.c.blocks]
   [nextjournal.devcards :as dc]
   [nextjournal.viewer :refer [inspect]]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

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
   :ui/current-rows
   [{::m.c.blocks/id       :foo
     ::m.c.blocks/fetched? (ds/gen-key ::m.c.blocks/fetched?)
     ::m.c.blocks/height   (ds/gen-key ::m.c.blocks/height)
     ::m.c.blocks/hash     (ds/gen-key ::m.c.blocks/hash)
     ::m.c.blocks/node     (ds/gen-key ::m.c.nodes/item)}
    {::m.c.blocks/id       :bar
     ::m.c.blocks/fetched? (ds/gen-key ::m.c.blocks/fetched?)
     ::m.c.blocks/height   (ds/gen-key ::m.c.blocks/height)
     ::m.c.blocks/hash     (ds/gen-key ::m.c.blocks/hash)
     ::m.c.blocks/node     (ds/gen-key ::m.c.nodes/item)}]
   :ui/loaded-data  []
   :ui/page-count   1
   :ui/parameters   {}})

(dc/defcard CoreBlockReport-data-card
  []
  [inspect (CoreBlockReport-data)])

(ws/defcard CoreBlockReport
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.c.blocks/CoreBlockReport
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state CoreBlockReport-data}))
