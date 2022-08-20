(ns dinsro.ui.core.node-wallets-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [dinsro.ui.core.node-wallets :as u.c.node-wallets]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn SubPage-row-node
  []
  {::m.c.nodes/id   (ds/gen-key ::m.c.nodes/id)
   ::m.c.nodes/name (ds/gen-key ::m.c.nodes/name)})

(defn SubPage-row-user
  []
  {::m.users/id   (ds/gen-key ::m.users/id)
   ::m.users/name (ds/gen-key ::m.users/name)})

(defn SubPage-row
  ([]
   (SubPage-row
    {:node (SubPage-row-node)
     :user (SubPage-row-user)}))
  ([{:keys [node user]
     :or
     {node (SubPage-row-node)
      user (SubPage-row-user)}}]
   {::m.c.wallets/id           (ds/gen-key ::m.c.wallets/id)
    ::m.c.wallets/name         (ds/gen-key ::m.c.wallets/name)
    ::m.c.wallets/derivation   (ds/gen-key ::m.c.wallets/derivation)
    ::m.c.wallets/key          (ds/gen-key ::m.c.wallets/key)
    ::m.c.wallets/user         user
    ::m.c.wallets/node node}))

(defn SubPage-report-data
  []
  (let [node (SubPage-row-node)
        user (SubPage-row-user)]
    {:foo             "bar"
     :ui/controls     []
     :ui/current-rows (map
                       (fn [_] (SubPage-row {:node node :user user}))
                       (range 3))
     :ui/busy?        false
     :ui/parameters   {}
     :ui/page-count   1
     :ui/current-page 1
     :ui/cache        {}}))

(defn SubPage-data
  []
  (let [initial-report-data (comp/get-initial-state u.c.node-wallets/SubPage)
        report-data         (merge initial-report-data (SubPage-report-data))]
    {::m.c.nodes/id (ds/gen-key ::m.c.nodes/id)
     :report        report-data}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard SubPage
  {::wsm/card-width 6 ::wsm/card-height 12}
  (th/fulcro-card u.c.node-wallets/SubPage SubPage-data {}))
