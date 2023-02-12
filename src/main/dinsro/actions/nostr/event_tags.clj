(ns dinsro.actions.nostr.event-tags
  (:require
   [lambdaisland.glogc :as log]))

(defn register-tag!
  [event-id tag]
  (log/info :register-tag!/start {:event-id event-id :tag tag}))
