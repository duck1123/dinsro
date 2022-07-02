^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.blocks-notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Block Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)
