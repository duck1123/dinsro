^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.node-base-notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Node Base

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)
