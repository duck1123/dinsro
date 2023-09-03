(ns dinsro.options.ln.peers
  (:require
   [dinsro.model.ln.peers :as m.ln.peers]))

(def id ::m.ln.peers/id)

(def node ::m.ln.peers/node)

(def remote-node ::m.ln.peers/remote-node)

(def inbound? ::m.ln.peers/inbound?)

(def sat-sent ::m.ln.peers/sat-sent)

(def sat-recv ::m.ln.peers/sat-recv)

(def address ::m.ln.peers/address)
