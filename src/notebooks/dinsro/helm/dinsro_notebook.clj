^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.helm.dinsro-notebook
  (:require
   [dinsro.helm.dinsro :as h.dinsro]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Dinsro Helm generator

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def inputs
  {:log-level :finest
   :portal {:enabled true}})

;; ## merge-defaults

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(def merged-defaults (h.dinsro/merge-defaults inputs))

;; ## ->dinsro-config

;; This generates the helm values file for dinsro

;; ^{::clerk/viewer clerk/code ::clerk/no-cache true}
;; (h.dinsro/->dinsro-config merged-defaults)

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(h.dinsro/->values-yaml merged-defaults)
