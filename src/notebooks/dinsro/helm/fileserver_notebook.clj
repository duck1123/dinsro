^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.helm.fileserver-notebook
  (:require
   [dinsro.helm.fileserver :as h.fileserver]
   [dinsro.helm.lnd :as h.lnd]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Helm generator

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def inputs {:name "notebooktest"
             :alias "Notebook Test"
             :auto-unlock {:password "hunter2"}
             :ingress {:host "fileserver.lnd.localhost"}
             :chain :mainnet})

;; ## merge-defaults

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(def merged-defaults (h.lnd/merge-defaults inputs))

;; ## ->values

;; This generates the helm values file for lnd

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(h.fileserver/->values merged-defaults)

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(h.fileserver/->values-yaml merged-defaults)
