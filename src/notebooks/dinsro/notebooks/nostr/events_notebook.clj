^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.notebooks.nostr.events-notebook
  (:require
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Nostr Events

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(ds/gen-key ::m.n.events/item)
