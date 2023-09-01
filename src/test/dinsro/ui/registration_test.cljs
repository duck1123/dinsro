(ns dinsro.ui.registration-test
  (:require
   [dinsro.mocks.ui.registration :as mo.u.registration]
   [dinsro.test-helpers :as th]
   [dinsro.ui.registration :as u.registration]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../main/dinsro/mocks/ui/registration.cljc]]
;; [[../../../main/dinsro/ui/registration.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard RegistrationIndexPage
  {::wsm/card-height 12
   ::wsm/card-width  4}
  (th/fulcro-card u.registration/IndexPage mo.u.registration/IndexPage-data {}))
