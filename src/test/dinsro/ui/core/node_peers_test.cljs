(ns dinsro.ui.core.node-peers-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.specs :as ds]
   [dinsro.ui.core.node-peers :as u.c.node-peers]))

(defn NodePeersSubPage-row
  []
  {::m.c.peers/id           (ds/gen-key ::m.c.peers/id)
   ::m.c.peers/addr         (ds/gen-key ::m.c.peers/addr)
   ::m.c.peers/address-bind (ds/gen-key ::m.c.peers/address-bind)
   ::m.c.peers/peer-id      (ds/gen-key ::m.c.peers/peer-id)
   ::m.c.peers/subver       (ds/gen-key ::m.c.peers/subver)
   ::m.c.peers/node         {::m.c.nodes/id   (ds/gen-key ::m.c.nodes/id)
                             ::m.c.nodes/name (ds/gen-key ::m.c.nodes/name)}})

(defn NodePeersSubPage-report-data
  []
  {:foo             "bar"
   :ui/controls     []
   :ui/current-rows (map (fn [_] (NodePeersSubPage-row)) (range 3))
   :ui/busy?        false
   :ui/parameters   {}
   :ui/page-count   1
   :ui/current-page 1
   :ui/cache        {}})

(defn NodePeersSubPage-data
  []
  (let [initial-report-data (comp/get-initial-state u.c.node-peers/NodePeersSubPage)
        report-data         (merge initial-report-data (NodePeersSubPage-report-data))]
    {::m.c.nodes/id (ds/gen-key ::m.c.nodes/id)
     :report        report-data}))
