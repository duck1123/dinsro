(ns dinsro.options.ln.remote-nodes
  (:refer-clojure :exclude [alias])
  (:require
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]))

(def id ::m.ln.remote-nodes/id)
(def pubkey ::m.ln.remote-nodes/pubkey)
(def host ::m.ln.remote-nodes/host)
(def color ::m.ln.remote-nodes/color)
(def alias ::m.ln.remote-nodes/alias)
(def node ::m.ln.remote-nodes/node)
(def num-channels ::m.ln.remote-nodes/num-channels)
