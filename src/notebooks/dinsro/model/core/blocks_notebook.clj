^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.model.core.blocks-notebook
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Blocks Model

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; ## params

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.c.blocks/params)

;; ## item

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.c.blocks/item)

^{::clerk/viewer clerk/table}
m.c.blocks/attributes
