(ns dinsro.actions.nostr.streams
  (:require
   [lambdaisland.glogc :as log]
   [manifold.stream :as st]))

;; [[../../../../notebooks/dinsro/notebooks/nostr/streams_notebook.clj]]

(defonce s (st/stream))
(defonce pubkey-stream (st/stream))

(defn start-consumer!
  ([] (start-consumer! "unset-id"))
  ([id]
   (let [r (st/consume
            (fn [m]
              (log/info :start-consumer!/recieved {:m m :id id}))
            s)]
     (log/info :start-consumer!/created {:r r :id id})
     r)))

(defn enqueue-pubkey-id!
  [pubkey-id]
  (log/info :enqueue-pubkey-id!/starting {:pubkey-id pubkey-id})
  (st/put! pubkey-stream pubkey-id))
