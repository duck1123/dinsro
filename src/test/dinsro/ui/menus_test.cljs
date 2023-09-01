(ns dinsro.ui.menus-test
  (:require
   [dinsro.mocks.ui.menus :as mo.u.menus]
   [dinsro.test-helpers :as th]
   [dinsro.ui.menus :as u.menus]
   [nubank.workspaces.core :as ws]))

;; [[../../../main/dinsro/mocks/ui/menus.cljc]]
;; [[../../../main/dinsro/ui/menus.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard NavMenu
  (th/fulcro-card u.menus/NavMenu mo.u.menus/NavMenu-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard VerticalMenu
  (th/fulcro-card u.menus/VerticalMenu mo.u.menus/VerticalMenu-data {}))
