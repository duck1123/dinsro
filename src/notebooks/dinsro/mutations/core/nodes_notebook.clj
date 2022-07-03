^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.mutations.core.nodes-notebook
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Nodes Mutations

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; ## fetch!

;; ### request

^{::clerk/viewer clerk/code}
(ds/gen-key ::mu.c.nodes/fetch!-request)

;; ### response

;; #### random

^{::clerk/viewer clerk/code}
(ds/gen-key ::mu.c.nodes/fetch!-response)

;; #### success

^{::clerk/viewer clerk/code}
(ds/gen-key ::mu.c.nodes/fetch!-response-success)

;; #### error

^{::clerk/viewer clerk/code}
(ds/gen-key ::mu.c.nodes/fetch!-response-error)

;; ## generate!

;; ### Request

^{::clerk/viewer clerk/code}
(ds/gen-key ::mu.c.nodes/generate!-request)

;; ## Fetch Peers
