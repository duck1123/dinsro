(ns dinsro.ui.core.words-test
  (:require
   [dinsro.client :as client]
   [dinsro.mocks.ui.core.words :as mo.u.c.words]
   [dinsro.ui.core.words :as u.c.words]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/ui/core/words.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreWordsReport
  {::wsm/card-width 6 ::wsm/card-height 12}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.words/Report
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (mo.u.c.words/Report-data))}))
