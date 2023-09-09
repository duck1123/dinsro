(ns dinsro.ui.forms.categories-test
  (:require
   [dinsro.mocks.ui.forms.categories :as mo.u.f.categories]
   [dinsro.test-helpers :as th]
   [dinsro.ui.forms.categories :as u.f.categories]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/mocks/ui/forms/categories.cljc]]
;; [[../../../../main/dinsro/ui/forms/categories.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CategoriesNewForm
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.categories/NewForm mo.u.f.categories/NewForm-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CategoryForm
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.categories/CategoryForm mo.u.f.categories/CategoryForm-data {}))
