(ns dinsro.streams
  (:require
   [clojure.spec.alpha :as s]
   [manifold.stream :as ms]))

(s/def ::stream-message (s/cat :source-key keyword?
                               :msg any?))

(defonce channels (atom {}))
(defonce message-source (ms/stream))
