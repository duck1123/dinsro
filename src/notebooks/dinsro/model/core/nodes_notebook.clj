^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.model.core.nodes-notebook
  (:require
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Nodes Model

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; ## Required Params

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.c.nodes/required-params)

;; ## Params

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.c.nodes/params)

;; ## Item

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.c.nodes/item)

^{::clerk/viewer clerk/table}
m.c.nodes/attributes
