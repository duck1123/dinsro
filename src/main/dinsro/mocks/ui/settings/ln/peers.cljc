(ns dinsro.mocks.ui.settings.ln.peers
  (:require
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.specs :as ds]))

;; [[../../../../model/ln/peers.cljc]]
;; [[../../../../ui/ln/peers.cljc]]
;; [[../../../../../../test/dinsro/ui/ln/peers_test.cljs]]

(defn Report-item
  []
  {::m.ln.peers/id          (ds/gen-key ::m.ln.peers/id)
   ::m.ln.peers/remote-node {::m.ln.nodes/id   (ds/gen-key ::m.ln.nodes/id)
                             ::m.ln.nodes/name (ds/gen-key ::m.ln.nodes/name)}
   ::m.ln.peers/node        {::m.ln.nodes/id   (ds/gen-key ::m.ln.nodes/id)
                             ::m.ln.nodes/name (ds/gen-key ::m.ln.nodes/name)}})

(defn NewForm-data
  []
  {::m.ln.peers/id          (ds/gen-key ::m.ln.peers/id)
   ::m.ln.peers/remote-node {::m.ln.nodes/id   (ds/gen-key ::m.ln.nodes/id)
                             ::m.ln.nodes/name (ds/gen-key ::m.ln.nodes/name)}
   ::m.ln.peers/node        {::m.ln.nodes/id   (ds/gen-key ::m.ln.nodes/id)
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
