(ns dinsro.actions.nostr.streams
  (:require
   [lambdaisland.glogc :as log]
   [manifold.deferred :as d]
   [manifold.stream :as st]))

(defonce s (st/stream))

(defn start-consumer!
  ([] (start-consumer! "unset-id"))
  ([id]
   (let [r (st/consume
            (fn [m]
              (log/info :start-consumer!/recieved {:m m :id id}))
            s)]
     (log/info :start-consumer!/created {:r r :id id})
     r)))

(comment

  s

  (d/realized? (st/try-take! s ::drained 1000 ::timeout))

  (st/put! s "foo")
  (st/put! s "bar")

  (st/stream->seq s)

  (start-consumer! "A")

  nil)
