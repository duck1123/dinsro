(ns dinsro.ui.ln.peers-test
  (:require
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [dinsro.ui.ln.peers :as u.ln.peers]
   [nubank.workspaces.core :as ws]))

(defn Report-item
  []
  {::m.ln.peers/id      (ds/gen-key ::m.ln.peers/id)
   ::m.ln.peers/address (ds/gen-key ::m.ln.peers/address)
   ::m.ln.peers/pubkey  (ds/gen-key ::m.ln.peers/pubkey)
   ::m.ln.peers/node    {::m.ln.nodes/id   (ds/gen-key ::m.ln.nodes/id)
                         ::m.ln.nodes/name (ds/gen-key ::m.ln.nodes/name)}})

(defn LNPeerForm-data
  []
  {::m.ln.peers/id      (ds/gen-key ::m.ln.peers/id)
   ::m.ln.peers/address (ds/gen-key ::m.ln.peers/address)
   ::m.ln.peers/pubkey  (ds/gen-key ::m.ln.peers/pubkey)})

(defn NewPeerForm-data
  []
  {::m.ln.peers/id      (ds/gen-key ::m.ln.peers/id)
   ::m.ln.peers/address (ds/gen-key ::m.ln.peers/address)
   ::m.ln.peers/pubkey  (ds/gen-key ::m.ln.peers/pubkey)
   ::m.ln.peers/node    {::m.ln.nodes/id   (ds/gen-key ::m.ln.nodes/id)
                         ::m.ln.nodes/name (ds/gen-key ::m.ln.nodes/name)}})

(defn Report-data
  []
  {:ui/busy?        false
   :ui/cache        {}
   :ui/controls     []
   :ui/current-page 1
   :ui/current-rows (map (fn [_] (Report-item)) (range 3))
   :ui/loaded-data  []
   :ui/page-count   1
   :ui/parameters   {}})

(ws/defcard Report
  (th/fulcro-card u.ln.peers/Report Report-data {}))

(ws/defcard NewPeerForm
  (th/fulcro-card u.ln.peers/NewPeerForm NewPeerForm-data {}))
