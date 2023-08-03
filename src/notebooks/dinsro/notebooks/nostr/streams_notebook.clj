(ns dinsro.notebooks.nostr.streams-notebook
  (:require
   [dinsro.actions.nostr.streams :as a.n.streams]
   [manifold.stream :as st]))

;; [[../../../../main/dinsro/actions/nostr/streams.clj]]

;; Streams

(str a.n.streams/pubkey-stream)

(comment

  (st/stream->seq a.n.streams/pubkey-stream)

  nil)
