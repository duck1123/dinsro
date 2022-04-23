(ns dinsro.ui.core.words-test
  (:require
   [dinsro.model.core.words :as m.c.words]
   [dinsro.client :as client]
   [dinsro.specs :as ds]
   [dinsro.ui.core.words :as u.c.words]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn WordsReport-row
  []
  {::m.c.words/id       (ds/gen-key ::m.c.words/id)
   ::m.c.words/position (ds/gen-key ::m.c.words/position)
   ::m.c.words/word     (ds/gen-key ::m.c.words/word)})

(defn WordsReport-data
  []
  {:ui/busy?        false
   :ui/cache        {}
   :ui/controls     []
   :ui/current-page 1
   :ui/current-rows (map (fn [_] (WordsReport-row)) (range 3))
   :ui/loaded-data  []
   :ui/page-count   1
   :ui/parameters   {}})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard WordsReport
  {::wsm/card-width 6 ::wsm/card-height 12}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.words/WordsReport
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (WordsReport-data))}))
