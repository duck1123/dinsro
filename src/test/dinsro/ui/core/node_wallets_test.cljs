(ns dinsro.ui.core.node-wallets-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.client :as client]
   [dinsro.specs :as ds]
   [dinsro.ui.core.node-wallets :as u.c.node-wallets]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn NodeWalletsSubPage-row
  []
  {::m.c.wallets/id   (ds/gen-key ::m.c.wallets/id)
   ::m.c.wallets/name (ds/gen-key ::m.c.wallets/name)
   ::m.c.wallets/user {::m.users/id   (ds/gen-key ::m.users/id)
                       ::m.users/name (ds/gen-key ::m.users/name)}
   ::m.c.wallets/node {::m.c.nodes/id   (ds/gen-key ::m.c.nodes/id)
                       ::m.c.nodes/name (ds/gen-key ::m.c.nodes/name)}})

(defn NodeWalletsSubPage-report-data
  []
  {:foo             "bar"
   :ui/controls     []
   :ui/current-rows (map (fn [_] (NodeWalletsSubPage-row)) (range 3))
   :ui/busy?        false
   :ui/parameters   {}
   :ui/page-count   1
   :ui/current-page 1
   :ui/cache        {}})

(defn NodeWalletsSubPage-data
  []
  (let [initial-report-data (comp/get-initial-state u.c.node-wallets/NodeWalletsSubPage)
        report-data         (merge initial-report-data (NodeWalletsSubPage-report-data))]
    {::m.c.nodes/id (ds/gen-key ::m.c.nodes/id)
     :report        report-data}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard NodeWalletsSubPage
  {::wsm/card-width 6 ::wsm/card-height 12}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.node-wallets/NodeWalletsSubPage
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (NodeWalletsSubPage-data))}))
