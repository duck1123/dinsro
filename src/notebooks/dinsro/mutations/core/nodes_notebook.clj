^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.mutations.core.nodes-notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.responses.core.nodes :as r.c.nodes]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; [../../../../../src/main/dinsro/mutations/core/nodes.cljc]

;; # Core Nodes Mutations

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; ## fetch!

;; ### request

^{::clerk/viewer clerk/code}
(ds/gen-key ::r.c.nodes/fetch!-request)

;; ### response

;; #### random

^{::clerk/viewer clerk/code}
(ds/gen-key ::r.c.nodes/fetch!-response)

;; #### success

^{::clerk/viewer clerk/code}
(ds/gen-key ::r.c.nodes/fetch!-response-success)

;; #### error

^{::clerk/viewer clerk/code}
(ds/gen-key ::r.c.nodes/fetch!-response-error)

;; ## generate!

;; ### Request

^{::clerk/viewer clerk/code}
(ds/gen-key ::r.c.nodes/generate!-request)

;; ## Fetch Peers
