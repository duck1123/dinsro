(ns dinsro.ui.home-test
  (:require
   [dinsro.mocks.ui.home :as mo.u.home]
   [dinsro.test-helpers :as th]
   [dinsro.ui.home :as u.home]
   [nubank.workspaces.core :as ws]))

;; [[../../../main/dinsro/mocks/ui/home.cljc]]
;; [[../../../main/dinsro/options/navlinks.cljc]]
;; [[../../../main/dinsro/ui/home.cljc]]
;; [[../../../notebooks/dinsro/notebooks/home_notebook.clj]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard Home
  (th/fulcro-card u.home/IndexPage mo.u.home/IndexPage-data {}))
