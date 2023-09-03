(ns dinsro.options.ln.nodes
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.ln.nodes :as m.ln.nodes]))

(def id ::m.ln.nodes/id)

(def name ::m.ln.nodes/name)

(def core-node ::m.ln.nodes/core-node)

(def user ::m.ln.nodes/user)