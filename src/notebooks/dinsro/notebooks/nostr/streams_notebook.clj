(ns dinsro.notebooks.nostr.streams-notebook
  (:require
   [dinsro.actions.nostr.streams :as a.n.streams]
   [manifold.deferred :as d]
   [manifold.stream :as st]))

;; [[../../../../main/dinsro/actions/nostr/streams.clj]]

;; Streams

(str a.n.streams/pubkey-stream)

(comment

  (st/stream->seq a.n.streams/pubkey-stream)

  nil)

(comment

  a.n.streams/s

  (d/realized? (st/try-take! a.n.streams/s ::drained 1000 ::timeout))

  (st/put! a.n.streams/s "foo")
  (st/put! a.n.streams/s "bar")

  (st/stream->seq a.n.streams/s)

  (st/stream->seq a.n.streams/pubkey-stream)

  (a.n.streams/start-consumer! "A")

  nil)
