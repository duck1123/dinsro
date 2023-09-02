(ns dinsro.notebooks.navlinks-notebook
  (:require
   [com.wsscode.pathom.connect :as pc]
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navlinks :as m.navlinks]))

;; [[../../../main/dinsro/joins/navlinks.cljc]]

@m.navlinks/routes-atom

;; ## Path (join)

(j.navlinks/find-path :admin-core-chains-show-networks)
(j.navlinks/find-path2 :admin-core-chains-show-networks)

((::pc/resolve j.navlinks/path) {} {::m.navlinks/id :admin-core-chains-show-networks})
