(ns dinsro.mocks.ui.core.nodes.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.ui.core.nodes.wallets :as u.c.n.wallets]))

;; [[../../../../ui/core/nodes/wallets.cljc]]
;; [[../../../../../../notebooks/dinsro/notebooks/core/wallets_notebook.clj]]

(def row-count 3)

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

(defn Report-data
  []
  (let [node (SubPage-row-node)
        user (SubPage-row-user)]
    {:foo             "bar"
     :ui/controls     []
     :ui/current-rows (map
                       (fn [_] (SubPage-row {:node node :user user}))
                       (range row-count))
     :ui/busy?        false
     :ui/parameters   {}
     :ui/page-count   1
     :ui/current-page 1
     :ui/cache        {}}))

(defn SubPage-data
  []
  (let [initial-report-data (comp/get-initial-state u.c.n.wallets/SubPage)
        report-data         (merge initial-report-data (Report-data))]
    {::m.c.nodes/id (ds/gen-key ::m.c.nodes/id)
     ::m.navlinks/id u.c.n.wallets/index-page-id
     :ui/report        report-data}))
