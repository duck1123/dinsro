(ns dinsro.ui.breadcrumbs-test
  (:require
   [dinsro.mocks.breadcrumbs :as mo.breadcrumbs]
   [dinsro.test-helpers :as th]
   [dinsro.ui.breadcrumbs :as u.breadcrumbs]
   [nubank.workspaces.core :as ws]))

;; [[../../../main/dinsro/mocks/breadcrumbs.cljc]]
;; [[../../../main/dinsro/options/navlinks.cljc]]
;; [[../../../main/dinsro/ui/breadcrumbs.cljc]]
;; [[../../../notebooks/dinsro/notebooks/breadcrumbs_notebook.clj]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard BreadcrumbLink
  (th/fulcro-card u.breadcrumbs/BreadcrumbLink mo.breadcrumbs/make-link {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard BreadcrumbsInner
  (th/fulcro-card u.breadcrumbs/BreadcrumbsInner mo.breadcrumbs/make-link {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard Breadcrumbs
  (th/fulcro-card u.breadcrumbs/Breadcrumbs mo.breadcrumbs/Breadcrumbs-data {}))
