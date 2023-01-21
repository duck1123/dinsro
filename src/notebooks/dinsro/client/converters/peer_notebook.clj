^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.client.converters.peer-notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

2
