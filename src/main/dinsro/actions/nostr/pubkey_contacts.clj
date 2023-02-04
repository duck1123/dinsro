(ns dinsro.actions.nostr.pubkey-contacts
  (:require
   [lambdaisland.glogc :as log]))

(defn fetch-contacts!
  []
  (log/info :fetch-contacts!/starting {}))
