(ns dinsro.options.nostr.connections
  (:require
   [dinsro.model.nostr.connections :as m.n.connections]))

(def end-time
  "The time when the connection ended"
  ::m.n.connections/end-time)

(def id
  "The id of a connection"
  ::m.n.connections/id)

(def instance
  "The instance of the server maintaining this connection"
  ::m.n.connections/instance)

(def start-time
  "The time when this connection was started"
  ::m.n.connections/start-time)

(def relay
  "The relay this connection is with"
  ::m.n.connections/relay)

(def status
  "The status of this connection"
  ::m.n.connections/status)
