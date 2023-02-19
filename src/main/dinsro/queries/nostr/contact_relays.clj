(ns dinsro.queries.nostr.contact-relays
  (:require
   [lambdaisland.glogc :as log]))

(defn find-by-relay
  [relay-id]
  (log/info :find-by-relay/starting {:relay-id relay-id}))
