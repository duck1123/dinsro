(ns dinsro.mocks.ui.core.words
  (:require
   [dinsro.model.core.words :as m.c.words]
   [dinsro.specs :as ds]))

(defn Report-row
  []
  {::m.c.words/id       (ds/gen-key ::m.c.words/id)
   ::m.c.words/position (ds/gen-key ::m.c.words/position)
   ::m.c.words/word     (ds/gen-key ::m.c.words/word)})

(defn Report-data
  []
  {:ui/busy?        false
   :ui/cache        {}
   :ui/controls     []
   :ui/current-page 1
   :ui/current-rows (map (fn [_] (Report-row)) (range 3))
   :ui/loaded-data  []
   :ui/page-count   1
   :ui/parameters   {}})
