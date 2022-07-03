^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.client.converters-notebook
  (:refer-clojure :exclude [BigDecimal])
  (:require
   [dinsro.client.converters :as c.converters]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Scala Client Converters

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(ds/gen-key ::c.converters/best-block-hash)
(ds/gen-key ::c.converters/blocks)
(ds/gen-key ::c.converters/chain)
(ds/gen-key ::c.converters/chainwork)
(ds/gen-key ::c.converters/difficulty)

(ds/gen-key ::c.converters/fetch-block-by-height-result)
