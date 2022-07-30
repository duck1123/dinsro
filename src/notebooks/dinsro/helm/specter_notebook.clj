^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.helm.specter-notebook
  (:require
   [dinsro.helm.specter :as h.specter]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Specter Helm generator

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def inputs {:name "alice"})

;; ## merge-defaults

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(def merged-defaults (h.specter/merge-defaults inputs))

;; ## ->node-config

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(h.specter/->node-config merged-defaults)

;; ## ->values

;; This generates the helm values file for specter

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(h.specter/->values merged-defaults)

;; ## ->values-yaml

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(h.specter/->values-yaml merged-defaults)
