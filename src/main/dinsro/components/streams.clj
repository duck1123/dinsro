(ns dinsro.components.streams
  (:require
   [manifold.stream :as ms]))

(defonce message-source (ms/stream))
