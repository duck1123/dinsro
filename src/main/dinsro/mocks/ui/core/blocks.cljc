(ns dinsro.mocks.ui.core.blocks
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.specs :as ds]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [lambdaisland.glogc :as log]))

;; [[../../../../../test/dinsro/ui/core/blocks_test.cljs]]

(defn refs-row-data
  [a]
  (log/info :refs-row-data/initial {:a a})
  {:ui/foo               "bar2"
   ::m.c.blocks/id       :foo
   ::m.c.blocks/fetched?
   (ds/gen-key ::m.c.blocks/fetched?)
   #_true
   ::m.c.blocks/height      (ds/gen-key ::m.c.blocks/height)
   ::m.c.blocks/hash        (ds/gen-key ::m.c.blocks/hash)})

(defn Report-data
  []
  {:ui/busy?        false
   :ui/cache        {}
   :ui/controls     []
   :ui/current-page 1
   :ui/current-rows (map (fn [_] (ds/gen-key ::u.c.blocks/row)) (range 3))
   :ui/loaded-data  []
   :ui/page-count   1
   :ui/parameters   {}})
