(ns dinsro.ui.core.blocks-test
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.client :as client]
   dinsro.machines
   [dinsro.mocks.ui.core.blocks :as mo.u.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.test-helpers :as th]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [lambdaisland.glogc :as log]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/mocks/ui/core/blocks.cljc]]
;; [[../../../../main/dinsro/ui/core/blocks.cljc]]

(defsc CoreBlocksRefRow-wrapper
  [_this props]
  {:ident         ::m.c.blocks/id
   :initial-state {::m.c.blocks/id       :foo
                   ::m.c.blocks/fetched? true
                   ::m.c.blocks/height   6
                   ::m.c.blocks/hash     "yes"}
   :query         [::m.c.blocks/id
                   ::m.c.blocks/fetched?
                   ::m.c.blocks/height
                   ::m.c.blocks/hash]}
  (log/info :CoreBlocksRefRow-wrapper/starting {:props props})
  (dom/table {}
    (dom/tbody {}
      ((comp/factory u.c.blocks/RefRow) props))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreBlocksRefRow
  {::wsm/card-width 6 ::wsm/card-height 13}
  (th/fulcro-card CoreBlocksRefRow-wrapper mo.u.c.blocks/refs-row-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreBlocksReport
  {::wsm/card-width 6 ::wsm/card-height 13}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.c.blocks/Report
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state mo.u.c.blocks/Report-data}))
