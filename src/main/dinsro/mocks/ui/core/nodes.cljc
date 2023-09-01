(ns dinsro.mocks.ui.core.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.mocks.ui.core.nodes.peers :as mo.u.c.n.peers]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.specs :as ds]
   [dinsro.ui.core.nodes :as u.c.nodes]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../../ui/core/nodes.cljc]]

(def gen? true)

(defn Show-data
  []
  (let [id (ds/gen-key ::m.c.nodes/id)
        data {::m.c.nodes/id          id
              ::m.c.nodes/chain       "regtest"
              ::m.c.nodes/block-count 73
              ::m.c.nodes/name        (if gen?
                                        (ds/gen-key ::m.c.nodes/name)
                                        "main node")
              ::m.c.nodes/fetched?    true
              ::m.c.nodes/height      6
              ::m.c.nodes/hash        "yes"
              :ui/nav-menu (comp/get-initial-state u.menus/NavMenu
                             {::m.navbars/id u.c.nodes/show-page-id
                              :id            id})
              :ui/router {}
              :peers                  (mo.u.c.n.peers/SubPage-data)}]
    (log/info :Show-data/response {:data data})
    data))
