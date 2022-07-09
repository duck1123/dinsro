^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.notebook-utils-notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Notebook Utils Notebook

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; for when you want to get meta

(def sample-props
  ({:f "src/notebooks/dinsro/actions/core/blocks_notebook.clj" :n 'dinsro.actions.core.blocks-notebook}
   {:f "src/notebooks/dinsro/actions/core/chains_notebook.clj" :n 'dinsro.actions.core.chains-notebook}
   {:f "src/notebooks/dinsro/actions/core/connections_notebook.clj" :n 'dinsro.actions.core.connections-notebook}))

(nu/display (nu/x2))

;; (nu/display-file-links sample-props)
